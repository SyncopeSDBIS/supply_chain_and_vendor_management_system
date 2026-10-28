package ro.uaic.feaa.sdbis.supplychain.facturi.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Public Contract DTO exposed by the Facturi / Accounts Payable module.
 */
public record FacturaSummaryDTO(
        UUID idFactura,
        String serieSiNumar,
        UUID comandaId,
        UUID furnizorId,
        LocalDate dataEmitere,
        LocalDate dataScadenta,
        BigDecimal totalDePlata,
        BigDecimal totalPlatit,
        String moneda,
        String stareFactura
) implements Serializable {}
