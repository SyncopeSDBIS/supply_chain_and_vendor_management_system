package ro.uaic.feaa.sdbis.supplychain.comenzi.api.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Public Contract DTO representing a line item in a purchase order.
 */
public record LinieComandaDTO(
        @NotBlank(message = "Codul de material este obligatoriu")
        @Size(min = 1, max = 50, message = "Codul materialului trebuie sa contina intre 1 si 50 caractere")
        String material,

        @NotNull(message = "Cantitatea este obligatorie")
        @Positive(message = "Cantitatea trebuie sa fie strict pozitiva")
        BigDecimal cantitate,

        @NotBlank(message = "Unitatea de masura este obligatorie")
        @Size(min = 1, max = 10, message = "Unitatea de masura trebuie sa aiba intre 1 si 10 caractere")
        String unitateMasura,

        @NotNull(message = "Pretul unitar este obligatoriu")
        @PositiveOrZero(message = "Pretul unitar trebuie sa fie pozitiv sau zero")
        BigDecimal pretUnitar,

        @NotBlank(message = "Moneda este obligatorie")
        @Pattern(regexp = "EUR|RON|USD", message = "Moneda trebuie sa fie una dintre: EUR, RON, USD")
        String moneda
) implements Serializable {}
