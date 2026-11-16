package ro.uaic.feaa.sdbis.supplychain.comenzi.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Public DTO exposing computed supplier delivery performance metrics (OTIF, price conformity)
 * across the modular monolith boundary.
 */
public record PerformantaLivrariDTO(
    UUID furnizorId,
    BigDecimal otif,
    BigDecimal conformitatePret,
    int totalComenzi,
    int comenziLivrateLaTimp
) implements Serializable {}

