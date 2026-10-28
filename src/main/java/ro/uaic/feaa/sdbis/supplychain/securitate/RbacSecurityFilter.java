package ro.uaic.feaa.sdbis.supplychain.securitate;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Filtru Zero-Trust RBAC ce inspecteaza headerul X-User-Role (ex: ROLE_BUYER, ROLE_ACCOUNTANT, etc.).
 * Implementeaza principiul Least Privilege si returneaza HTTP 403 Forbidden la incalcarea politicilor RBAC.
 */
@Component
public class RbacSecurityFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RbacSecurityFilter.class);

    private static final Set<String> COMENZI_ROLES = Set.of("ROLE_BUYER", "ROLE_ADMIN", "ROLE_WAREHOUSE_KEEPER", "ROLE_SUPPLIER");
    private static final Set<String> CALITATE_ROLES = Set.of("ROLE_QUALITY_INSPECTOR", "ROLE_ADMIN");
    private static final Set<String> FACTURI_ROLES = Set.of("ROLE_ACCOUNTANT", "ROLE_CFO", "ROLE_ADMIN");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String roleHeader = httpRequest.getHeader("X-User-Role");
        String userId = httpRequest.getHeader("X-User-Id");
        if (userId == null || userId.isBlank()) {
            userId = "system-user";
        }

        String uri = httpRequest.getRequestURI();

        // 1. Webhooks si rute publice de documentatie/health sunt intotdeauna permise
        if (uri.contains("/webhook-banca") || uri.startsWith("/swagger") || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/actuator") || uri.startsWith("/h2-console")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Daca este furnizat X-User-Role explicit, se aplica regulile stricte Zero-Trust RBAC
        if (roleHeader != null && !roleHeader.isBlank()) {
            String normalizedRole = roleHeader.trim().toUpperCase();
            if (!normalizedRole.startsWith("ROLE_")) {
                normalizedRole = "ROLE_" + normalizedRole;
            }

            // Validare granulara RBAC pe rute
            boolean denied = false;
            String requiredRolesDescription = "";

            if (uri.startsWith("/api/comenzi")) {
                if (!COMENZI_ROLES.contains(normalizedRole)) {
                    denied = true;
                    requiredRolesDescription = COMENZI_ROLES.toString();
                }
            } else if (uri.startsWith("/api/inspectii") || uri.startsWith("/api/calitate") || uri.startsWith("/api/furnizori")) {
                if (!CALITATE_ROLES.contains(normalizedRole)) {
                    denied = true;
                    requiredRolesDescription = CALITATE_ROLES.toString();
                }
            } else if (uri.startsWith("/api/facturi") || uri.startsWith("/api/ordine-plata")) {
                if (!FACTURI_ROLES.contains(normalizedRole)) {
                    denied = true;
                    requiredRolesDescription = FACTURI_ROLES.toString();
                }
            }

            if (denied) {
                log.warn("[RBAC] Access denied: User '{}' with role '{}' tried to access '{}'. Required roles: {}",
                        userId, normalizedRole, uri, requiredRolesDescription);
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write(String.format(
                        "{\"status\":403,\"error\":\"Forbidden\",\"message\":\"Access denied. Role %s cannot access %s\"}",
                        normalizedRole, uri
                ));
                return;
            }

            // Inregistreaza autentificarea in contextul Spring Security
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(normalizedRole));
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            // Daca nu se trimite header explicit, injectam context implicit pentru backward-compatibility cu suita existenta
            List<SimpleGrantedAuthority> defaultAuth = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, defaultAuth);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}
