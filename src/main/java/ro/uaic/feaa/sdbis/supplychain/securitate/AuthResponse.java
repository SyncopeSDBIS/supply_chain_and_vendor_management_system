package ro.uaic.feaa.sdbis.supplychain.securitate;

import java.util.UUID;

public record AuthResponse(
    String token,
    String tokenType,
    long expiresIn,
    String username,
    String role,
    UUID userId,
    String email,
    String fullName
) {}

