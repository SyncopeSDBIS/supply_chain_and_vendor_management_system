package ro.uaic.feaa.sdbis.supplychain.facturi.api.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Public Contract DTO exposed by the Facturi / Accounts Payable module.
 */
public record FacturaSummaryDTO(
        @NotNull(message = "IdFactura este obligatoriu")
        UUID idFactura,

        @NotBlank(message = "Seria si numarul facturii sunt obligatorii")
        String serieSiNumar,

        @NotNull(message = "ComandaId este obligatoriu")
        UUID comandaId,

        @NotNull(message = "FurnizorId este obligatoriu")
        UUID furnizorId,

        @NotNull(message = "Data emiterii este obligatorie")
        LocalDate dataEmitere,

        @NotNull(message = "Data scadentei este obligatorie")
        LocalDate dataScadenta,

        @NotNull(message = "Totalul de plata este obligatoriu")
        @PositiveOrZero(message = "Totalul de plata trebuie sa fie pozitiv sau zero")
        BigDecimal totalDePlata,

        @NotNull(message = "Totalul platit este obligatoriu")
        @PositiveOrZero(message = "Totalul platit trebuie sa fie pozitiv sau zero")
        BigDecimal totalPlatit,

        @NotBlank(message = "Moneda este obligatorie")
        @Pattern(regexp = "EUR|RON|USD", message = "Moneda trebuie sa fie una dintre: EUR, RON, USD")
        String moneda,

        @NotBlank(message = "Starea facturii este obligatorie")
        String stareFactura
) implements Serializable {}
