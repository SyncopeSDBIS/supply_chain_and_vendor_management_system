package ro.uaic.feaa.sdbis.supplychain.comenzi.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Public Contract DTO exposed by the Comenzi module for inter-module integration (e.g. 3-Way Matching).
 */
public record ComandaSummaryDTO(
        @NotNull(message = "IdComanda este obligatoriu")
        UUID idComanda,

        @NotBlank(message = "Numarul comenzii este obligatoriu")
        String numarComanda,

        @NotNull(message = "FurnizorId este obligatoriu")
        UUID furnizorId,

        @NotBlank(message = "Starea comenzii este obligatorie")
        String stareComanda,

        @NotNull(message = "Data livrarii estimate este obligatorie")
        LocalDate dataLivrareEstimata,

        @NotEmpty(message = "Liniile comenzii sunt obligatorii")
        List<@Valid @NotNull LinieComandaDTO> linii
) implements Serializable {}
