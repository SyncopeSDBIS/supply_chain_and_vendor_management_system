package ro.uaic.feaa.sdbis.supplychain.calitate.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

public record CreeazaFurnizorCommand(
    UUID id,
    @NotBlank(message = "Codul fiscal este obligatoriu")
    @Size(min = 2, max = 30)
    String codFiscal,

    @NotBlank(message = "Denumirea este obligatorie")
    @Size(min = 2, max = 255)
    String denumire,

    String strada,
    String oras,
    String codPostal,
    String tara,

    @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z0-9]{4,30}$", message = "Format IBAN invalid")
    String iban,

    Set<String> emailuri,
    Set<String> categorii,

    @PositiveOrZero(message = "Termenul de plata trebuie sa fie pozitiv sau zero")
    int termenPlataZile
) {}
