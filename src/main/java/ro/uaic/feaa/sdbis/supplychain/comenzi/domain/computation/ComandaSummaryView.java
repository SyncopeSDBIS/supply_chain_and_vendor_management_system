package ro.uaic.feaa.sdbis.supplychain.comenzi.domain.computation;

import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Entity View Computation: Proiectie sumarizata pentru comanda de aprovizionare (Sprint 2 - ADR-003).
 */
@ValueObject
public record ComandaSummaryView(
    IdComanda id,
    String numarComanda,
    BigDecimal subtotal,
    BigDecimal tva,
    BigDecimal totalCuTVA,
    double procentLivrare,
    int numarLinii,
    boolean esteLivrataComplet
) implements Serializable {}
