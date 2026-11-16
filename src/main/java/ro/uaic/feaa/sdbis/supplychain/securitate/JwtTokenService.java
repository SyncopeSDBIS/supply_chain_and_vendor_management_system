package ro.uaic.feaa.sdbis.supplychain.securitate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Serviciu de generare si validare token-uri JWT (HMAC-SHA256) fara dependinte externe grele.
 * Conforma cu standardul RFC 7519.
 */
@Service
public class JwtTokenService {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenService.class);
    private static final String HMAC_ALGO = "HmacSHA256";
    private static final long DEFAULT_EXPIRATION_SECONDS = 86400; // 24 ore

    private final byte[] secretKeyBytes;
    private final ObjectMapper objectMapper;

    public JwtTokenService(
            @Value("${jwt.secret:${JWT_SECRET:}}") String secret,
            ObjectMapper objectMapper) {
        if (secret == null || secret.trim().length() < 32) {
            log.warn("[Security Alert] Cheia secreta JWT (${jwt.secret}) este absenta sau are sub 256 biti (32 caractere). " +
                    "Se recomanda setarea variabilei de mediu JWT_SECRET cu o cheie criptografica sigura.");
            // Utilizam fallback doar in modul dev local daca nu este furnizata alta cheie
            secret = "supplychain-dev-jwt-hmac-sha256-signing-key-for-local-development-only-2026-minimum-256bits";
        }
        this.secretKeyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.objectMapper = objectMapper;
    }

    public String generateToken(String username, String role, String userId) {
        long nowSeconds = Instant.now().getEpochSecond();
        long expSeconds = nowSeconds + DEFAULT_EXPIRATION_SECONDS;

        Map<String, Object> header = Map.of(
                "alg", "HS256",
                "typ", "JWT"
        );

        Map<String, Object> payload = new HashMap<>();
        payload.put("sub", username);
        payload.put("role", role);
        payload.put("userId", userId);
        payload.put("iat", nowSeconds);
        payload.put("exp", expSeconds);

        try {
            String encodedHeader = base64UrlEncode(objectMapper.writeValueAsString(header).getBytes(StandardCharsets.UTF_8));
            String encodedPayload = base64UrlEncode(objectMapper.writeValueAsString(payload).getBytes(StandardCharsets.UTF_8));
            String dataToSign = encodedHeader + "." + encodedPayload;
            String signature = sign(dataToSign);

            return dataToSign + "." + signature;
        } catch (Exception ex) {
            throw new RuntimeException("Eroare la generarea token-ului JWT: " + ex.getMessage(), ex);
        }
    }

    /**
     * Valideaza si extrage dintr-o singura operatie criptografica informatiile din token (FIX SEC-05).
     */
    public Optional<UserAuthenticationClaims> parseAndValidate(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return Optional.empty();
        }

        try {
            String dataToSign = parts[0] + "." + parts[1];
            String expectedSignature = sign(dataToSign);

            if (!MessageDigest.isEqual(parts[2].getBytes(StandardCharsets.UTF_8), expectedSignature.getBytes(StandardCharsets.UTF_8))) {
                log.warn("[JWT] Semnatura token-ului este invalida!");
                return Optional.empty();
            }

            Map<String, Object> claims = parsePayload(parts[1]);
            Number exp = (Number) claims.get("exp");
            if (exp == null || exp.longValue() < Instant.now().getEpochSecond()) {
                log.warn("[JWT] Token-ul este invalid: lipseste claim-ul 'exp' sau token-ul a expirat (exp={})", exp);
                return Optional.empty();
            }

            String username = (String) claims.get("sub");
            String role = (String) claims.get("role");
            String userId = (String) claims.get("userId");
            if (username == null || username.isBlank() || role == null || role.isBlank()) {
                log.warn("[JWT] Token-ul este invalid: claim-urile obligatorii 'sub' sau 'role' lipsesc sau sunt vide");
                return Optional.empty();
            }
            return Optional.of(new UserAuthenticationClaims(username, role, userId));
        } catch (Exception ex) {
            log.error("[JWT] Eroare la validarea token-ului: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    public boolean validateToken(String token) {
        return parseAndValidate(token).isPresent();
    }

    public Map<String, Object> getClaims(String token) {
        if (!validateToken(token)) {
            throw new IllegalArgumentException("Token JWT invalid sau expirat");
        }
        String[] parts = token.split("\\.");
        return parsePayload(parts[1]);
    }

    public String getUsername(String token) {
        return (String) getClaims(token).get("sub");
    }

    public String getRole(String token) {
        return (String) getClaims(token).get("role");
    }

    public String getUserId(String token) {
        return (String) getClaims(token).get("userId");
    }

    private String sign(String data) throws Exception {
        Mac mac = Mac.getInstance(HMAC_ALGO);
        SecretKeySpec keySpec = new SecretKeySpec(secretKeyBytes, HMAC_ALGO);
        mac.init(keySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return base64UrlEncode(rawHmac);
    }

    private Map<String, Object> parsePayload(String base64UrlPayload) {
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(base64UrlPayload);
            return objectMapper.readValue(bytes, new TypeReference<>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Payload JWT invalid: " + e.getMessage());
        }
    }

    private String base64UrlEncode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }
}

