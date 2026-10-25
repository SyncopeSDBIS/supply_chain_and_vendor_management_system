package ro.uaic.feaa.sdbis.supplychain.facturi.domain.computation;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Entity View Computation: Proiectie sumarizata a indicatorilor de plati si facturi (Sprint 2 - ADR-003).
 */
public record PlatiSummaryView(
    BigDecimal totalFacturat,
    BigDecimal totalAchitat,
    BigDecimal totalRestant,
    int numarFacturi,
    int facturiAchitateIntegral,
    int facturiRestante
) implements Serializable {}
