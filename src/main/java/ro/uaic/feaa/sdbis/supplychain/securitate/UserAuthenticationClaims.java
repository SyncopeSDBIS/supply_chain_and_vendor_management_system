package ro.uaic.feaa.sdbis.supplychain.securitate;

/**
 * Value Object imutabil reprezentand datele de autentificare extrase din token-ul JWT.
 */
public record UserAuthenticationClaims(String username, String role, String userId) {}
