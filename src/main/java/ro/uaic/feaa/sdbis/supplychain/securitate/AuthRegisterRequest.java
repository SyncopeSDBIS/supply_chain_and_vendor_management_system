package ro.uaic.feaa.sdbis.supplychain.securitate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRegisterRequest(
    @NotBlank(message = "Username-ul este obligatoriu")
    String username,

    @NotBlank(message = "Parola este obligatorie")
    String password,

    @NotBlank(message = "Email-ul este obligatoriu")
    @Email(message = "Formatul email-ului este invalid")
    String email,

    @NotBlank(message = "Numele complet este obligatoriu")
    String fullName,

    @NotBlank(message = "Rolul este obligatoriu")
    String role
) {}

