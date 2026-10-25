package ro.uaic.feaa.sdbis.supplychain.facturi.domain.validation;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.ComandaSummaryVO;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.NIR;

import java.math.BigDecimal;

/**
 * Contract pentru Serviciul de Validare a Domeniului Facturare & Reconciliere (ADR-003).
 * Respecta izolarea stricta a Bounded Context-ului facturi (ADR-011).
 */
public interface IFacturaValidationService {

    /**
     * Valideaza datele de baza la inregistrarea unei facturi de la furnizor.
     */
    void valideazaFacturaInregistrare(FacturaFurnizor factura);

    /**
     * Valideaza reconcilierea 3-Way Match (Comanda vs NIR vs Factura) conform tolerantei stabilite.
     */
    void valideaza3WayMatch(FacturaFurnizor factura, NIR nir, ComandaSummaryVO comanda, double tolerantaProcentualaMax);

    /**
     * Valideaza emiterea unui ordin de plata in raport cu starea facturii si restul de plata.
     */
    void valideazaOrdinPlata(FacturaFurnizor factura, BigDecimal sumaPlata);
}
