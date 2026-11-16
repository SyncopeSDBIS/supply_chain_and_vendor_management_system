package ro.uaic.feaa.sdbis.supplychain.securitate;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Filtru Anti-Abuz / Rate Limiting bazat pe Token Bucket (ADR-008, FIX SEC-04).
 * Limiteaza numarul de request-uri per client IP pentru a proteja sistemul impotriva DoS / spam.
 * 
 * NOTA ARHITECTURALA PENTRU SCALARE DISTRIBUITA:
 * In medii de cluster / multi-instanta (Kubernetes, Docker Swarm), acest mecanism in-memory
 * este completat/delegat catre un store centralizat Redis (prin Redis RateLimiter / Token Bucket lua script)
 * sau la nivel de Ingress / API Gateway (Envoy, Kong, AWS ALB).
 */
@Component
@Order(1)
public class RateLimitingFilter implements Filter {

    private static final int MAX_REQUESTS_PER_MINUTE = 300;
    private final ConcurrentHashMap<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();
    private final AtomicLong lastCleanupMinute = new AtomicLong(0);

    private static class RequestCounter {
        final long windowStart;
        final AtomicInteger count;

        RequestCounter(long windowStart) {
            this.windowStart = windowStart;
            this.count = new AtomicInteger(1);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();

        // Nu limitam rutele interne de monitorizare sau Swagger
        if (uri.startsWith("/actuator") || uri.startsWith("/swagger") || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/h2-console")) {
            chain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(httpRequest);
        long nowMinute = System.currentTimeMillis() / 60000;

        // FIX SEC-04: Politica activa de curatare a ferestrelor expirate fara blocaje (lock-free) la fiecare minut nou
        long lastCleanup = lastCleanupMinute.get();
        if (nowMinute > lastCleanup && lastCleanupMinute.compareAndSet(lastCleanup, nowMinute)) {
            requestCounts.entrySet().removeIf(entry -> entry.getValue().windowStart < nowMinute);
        }

        RequestCounter counter = requestCounts.compute(clientIp, (key, existing) -> {
            if (existing == null || existing.windowStart != nowMinute) {
                return new RequestCounter(nowMinute);
            }
            existing.count.incrementAndGet();
            return existing;
        });

        if (counter.count.get() > MAX_REQUESTS_PER_MINUTE) {
            httpResponse.setStatus(429); // 429 Too Many Requests
            httpResponse.setHeader("Retry-After", "60");
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                    "{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Rata maxima de cereri a fost depasita. Va rugam asteptati un minut.\"}"
            );
            return;
        }

        chain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr() != null ? request.getRemoteAddr() : "unknown-ip";
        if (isTrustedProxy(remoteAddr)) {
            String xf = request.getHeader("X-Forwarded-For");
            if (xf != null && !xf.isBlank()) {
                String candidate = xf.split(",")[0].trim();
                if (!candidate.isBlank()) {
                    return candidate;
                }
            }
        }
        return remoteAddr;
    }

    private boolean isTrustedProxy(String ip) {
        return "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || "localhost".equalsIgnoreCase(ip);
    }
}

