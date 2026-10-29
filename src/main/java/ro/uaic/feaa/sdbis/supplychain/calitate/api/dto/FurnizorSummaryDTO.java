package ro.uaic.feaa.sdbis.supplychain.calitate.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

/**
 * Public Contract DTO exposed by the Calitate / Vendor module.
 */
public record FurnizorSummaryDTO(
        @NotNull(message = "IdFurnizor este obligatoriu")
        UUID idFurnizor,

        @NotBlank(message = "Codul fiscal este obligatoriu")
        @Size(min = 2, max = 30)
        String codFiscal,

        @NotBlank(message = "Denumirea furnizorului este obligatorie")
        @Size(min = 2, max = 255)
        String denumire,

        @NotBlank(message = "Starea furnizorului este obligatorie")
        String stareFurnizor,

        @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z0-9]{4,30}$", message = "Format IBAN invalid")
        String iban,

        Set<String> categoriiMateriale,
        boolean esteEligibilPentruComenzi,
        boolean esteBlocat
) implements Serializable {}
