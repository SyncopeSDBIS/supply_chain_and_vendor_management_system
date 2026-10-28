package ro.uaic.feaa.sdbis.supplychain.securitate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configurare Spring Security pentru arhitectura Stateless REST (ADR-007)
 * si integrarea filtrului Zero-Trust RBAC (RbacSecurityFilter).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final RbacSecurityFilter rbacSecurityFilter;

    public SecurityConfig(RbacSecurityFilter rbacSecurityFilter) {
        this.rbacSecurityFilter = rbacSecurityFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> {})
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/api-docs/**",
                    "/actuator/**",
                    "/h2-console/**"
                ).permitAll()
                .anyRequest().permitAll()
            )
            .addFilterBefore(rbacSecurityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
