package ro.uaic.feaa.sdbis.supplychain.securitate;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequest(
    @NotBlank(message = "Username-ul este obligatoriu")
    String username,

    @NotBlank(message = "Parola este obligatorie")
    String password
) {}

