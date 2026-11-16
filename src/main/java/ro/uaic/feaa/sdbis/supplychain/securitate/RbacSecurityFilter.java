package ro.uaic.feaa.sdbis.supplychain.securitate;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Filtru Zero-Trust RBAC ce inspecteaza fie tokenul criptografic JWT (Authorization: Bearer <token>),
 * fie headerul de compatibilitate X-User-Role (ex: ROLE_BUYER, ROLE_ACCOUNTANT, etc.).
 * Implementeaza principiul Least Privilege si returneaza HTTP 403 Forbidden la incalcarea politicilor RBAC
 * sau HTTP 401 Unauthorized cand tokenul este invalid sau lipseste in modul hardened.
 */
@Component
public class RbacSecurityFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RbacSecurityFilter.class);

    private static final Set<String> COMENZI_ROLES = Set.of(
            "ROLE_BUYER", "ROLE_ADMIN", "ROLE_WAREHOUSE_KEEPER", "ROLE_SUPPLIER",
            "ROLE_REFERENT_APROVIZIONARE", "ROLE_FURNIZOR", "ROLE_MANAGER_ACHIZITII"
    );
    private static final Set<String> CALITATE_ROLES = Set.of(
            "ROLE_QUALITY_INSPECTOR", "ROLE_ADMIN", "ROLE_QUALITY_MANAGER",
            "ROLE_INSPECTOR_CALITATE", "ROLE_MANAGER_CALITATE", "ROLE_DIRECTOR_CALITATE"
    );
    private static final Set<String> FACTURI_ROLES = Set.of(
            "ROLE_ACCOUNTANT", "ROLE_CFO", "ROLE_ADMIN",
            "ROLE_CONTABIL", "ROLE_OFITER_FINANCIAR", "ROLE_DIRECTOR_FINANCIAR"
    );

    private final JwtTokenService jwtTokenService;

    @Value("${security.mock-headers.enabled:false}")
    private boolean mockHeadersEnabled = false;

    @Value("${security.allow-anonymous-fallback:false}")
    private boolean allowAnonymousFallback = false;

    public RbacSecurityFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    public void setMockHeadersEnabled(boolean mockHeadersEnabled) {
        this.mockHeadersEnabled = mockHeadersEnabled;
    }

    public void setAllowAnonymousFallback(boolean allowAnonymousFallback) {
        this.allowAnonymousFallback = allowAnonymousFallback;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();

        // 1. Rute publice: autentificare, documentatie, webhooks, health, documente atasate
        if (isPublicRoute(uri)) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Extragere rol si userId din JWT (Bearer token) sau strict din Mock Headers daca sunt activate
        String authHeader = httpRequest.getHeader("Authorization");
        String normalizedRole = null;
        String userId = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            java.util.Optional<UserAuthenticationClaims> claimsOpt = jwtTokenService.parseAndValidate(token);
            if (claimsOpt.isPresent()) {
                UserAuthenticationClaims claims = claimsOpt.get();
                normalizedRole = claims.role();
                userId = claims.username();
            } else {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Token JWT invalid sau expirat\"}");
                return;
            }
        } else if (mockHeadersEnabled) {
            String roleHeader = httpRequest.getHeader("X-User-Role");
            String rawUserId = httpRequest.getHeader("X-User-Id");
            if (roleHeader != null && !roleHeader.isBlank()) {
                log.warn("[RBAC Security Notice] Request-ul pentru '{}' foloseste headerul mock nesemnat 'X-User-Role'. Aceasta optiune este permisa exclusiv in teste.", uri);
                normalizedRole = roleHeader.trim();
                userId = (rawUserId != null && !rawUserId.isBlank()) ? rawUserId.trim() : "mock-test-user-" + java.util.UUID.randomUUID().toString().substring(0, 8);
            }
        } else {
            String roleHeader = httpRequest.getHeader("X-User-Role");
            if (roleHeader != null && !roleHeader.isBlank()) {
                log.warn("[RBAC Alert] Headerul 'X-User-Role' este strict blocat in mediul curent de securitate (mock-headers.enabled=false).");
            }
        }

        if (userId == null || userId.isBlank()) {
            userId = "authenticated-user";
        }

        // 3. Fallback anonim sau respingere daca nu exista rol valid
        if (normalizedRole == null || normalizedRole.isBlank()) {
            if (allowAnonymousFallback) {
                log.debug("[RBAC Compatibility] Request anonim autorizat implicit pentru testare locala: {}", uri);
                normalizedRole = "ROLE_ADMIN";
            } else {
                log.warn("[RBAC Security Alert] Cerere neautorizata pe resursa protejata '{}'. Lipseste token JWT valid.", uri);
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Autentificare obligatorie prin token Bearer JWT pentru accesarea acestei resurse\"}");
                return;
            }
        }

        // 4. Se aplica regulile de autorizare RBAC
        normalizedRole = normalizedRole.trim().toUpperCase();
        if (!normalizedRole.startsWith("ROLE_")) {
            normalizedRole = "ROLE_" + normalizedRole;
        }

        boolean denied = false;
        String requiredRolesDescription = "";

        if (uri.startsWith("/api/comenzi") || uri.startsWith("/api/loturi") || uri.startsWith("/api/expedieri")) {
            if (!COMENZI_ROLES.contains(normalizedRole)) {
                denied = true;
                requiredRolesDescription = COMENZI_ROLES.toString();
            }
        } else if (uri.startsWith("/api/inspectii") || uri.startsWith("/api/calitate")
                || uri.startsWith("/api/furnizori") || uri.startsWith("/api/inspectori")
                || uri.startsWith("/api/ncr")) {
            if (!CALITATE_ROLES.contains(normalizedRole)) {
                denied = true;
                requiredRolesDescription = CALITATE_ROLES.toString();
            }
        } else if (uri.startsWith("/api/facturi") || uri.startsWith("/api/ordine-plata")) {
            if (!FACTURI_ROLES.contains(normalizedRole)) {
                denied = true;
                requiredRolesDescription = FACTURI_ROLES.toString();
            }
        } else if (uri.startsWith("/api/nir")) {
            if (!FACTURI_ROLES.contains(normalizedRole) && !COMENZI_ROLES.contains(normalizedRole)) {
                denied = true;
                requiredRolesDescription = FACTURI_ROLES.toString() + " or " + COMENZI_ROLES.toString();
            }
        } else if (uri.startsWith("/api/documente")) {
            // FIX SEC-03: Roluri autorizate pentru gestiunea documentelor
            Set<String> DOC_ROLES = Set.of(
                "ROLE_ADMIN", "ROLE_BUYER", "ROLE_QUALITY_INSPECTOR", "ROLE_ACCOUNTANT",
                "ROLE_CFO", "ROLE_WAREHOUSE_KEEPER", "ROLE_SUPPLIER"
            );
            if (!DOC_ROLES.contains(normalizedRole) && !"ROLE_ADMIN".equals(normalizedRole)) {
                denied = true;
                requiredRolesDescription = DOC_ROLES.toString();
            }
        } else {
            // FIX SEC-03: Principiul Deny by Default (Zero-Trust) pentru orice ruta protejata necunoscuta
            if (!"ROLE_ADMIN".equals(normalizedRole)) {
                denied = true;
                requiredRolesDescription = "[ROLE_ADMIN]";
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

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(normalizedRole));
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            chain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private boolean isPublicRoute(String uri) {
        if (uri.startsWith("/api/auth") || uri.startsWith("/api/webhooks")
                || uri.equals("/api/ordine-plata/webhook-banca") || uri.equals("/api/plati/webhook-banca")
                || uri.startsWith("/api/facturi/webhook-banca")
                || uri.startsWith("/swagger") || uri.startsWith("/v3/api-docs") || uri.startsWith("/api-docs")
                || uri.startsWith("/actuator") || uri.startsWith("/h2-console")) {
            return true;
        }
        return false;
    }
}
