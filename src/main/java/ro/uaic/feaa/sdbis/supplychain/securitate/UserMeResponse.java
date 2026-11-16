package ro.uaic.feaa.sdbis.supplychain.securitate;

import java.util.UUID;

/**
 * DTO tipizat pentru endpoint-ul GET /api/auth/me (FIX REST-01).
 */
public record UserMeResponse(
        UUID id,
        String username,
        String email,
        String fullName,
        String role
) {}
