package ro.uaic.feaa.sdbis.supplychain.facturi.domain.computation;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainComputationService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Contract pentru Serviciul de Calcul Financiar si Reconciliere (Sprint 2 - ADR-003).
 */
@DomainComputationService
public interface IReconciliereComputationService {

    /**
     * Entity Field Computation: Calculeaza restul total de plata agregat dintr-o lista de facturi.
     */
    BigDecimal calculeazaRestPlataTotal(List<FacturaFurnizor> facturi);

    /**
     * Entity View Computation: Agrega indicatorii financiari si de plata intr-un tablou de bord.
     */
    PlatiSummaryView calculeazaIndicatoriPlati(List<FacturaFurnizor> facturi);

    /**
     * Calculeaza procentul de achitare a unei facturi (0.0 - 100.0%).
     */
    double calculeazaProcentAchitare(FacturaFurnizor factura);
}
