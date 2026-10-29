package ro.uaic.feaa.sdbis.supplychain.comenzi.api.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.dto.LinieComandaDTO;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Public Command for creating a purchase order via the module's public API contract.
 * Fully validated and self-checking across bounded context boundaries.
 */
public record CreeazaComandaPublicCommand(
        @NotNull(message = "FurnizorId este obligatoriu")
        UUID furnizorId,

        @NotNull(message = "GestionarId este obligatoriu")
        UUID gestionarId,

        @NotNull(message = "Data estimata de livrare este obligatorie")
        @FutureOrPresent(message = "Data livrarii estimate nu poate fi in trecut")
        LocalDate dataLivrareEstimata,

        @NotBlank(message = "Depozitul destinatie este obligatoriu")
        @Size(min = 2, max = 100, message = "Numele depozitului trebuie sa aiba intre 2 si 100 caractere")
        String depozitDestinatie,

        @NotEmpty(message = "Comanda trebuie sa contina cel putin o linie")
        List<@Valid @NotNull LinieComandaDTO> linii
) implements Serializable {}
