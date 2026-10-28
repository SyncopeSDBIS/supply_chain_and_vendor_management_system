package ro.uaic.feaa.sdbis.supplychain.facturi.domain.factory;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.LinieFactura;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainFactory;
import ro.uaic.feaa.sdbis.supplychain.shared.exception.DomainValidationException;

import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Factory pentru FacturaFurnizor (Sprint 2 - ADR-003).
 * Garanteaza initializarea consistenta a facturii in starea initiala Inregistrata
 * si calculul invariant al valorilor de linie.
 */
@DomainFactory
public class FacturaFactory {

    public static FacturaFurnizor creeaza(String serie,
                                          String numar,
                                          LocalDate dataEmitere,
                                          LocalDate dataScadenta,
                                          IdComanda comandaId,
                                          IdFurnizor furnizorId,
                                          List<LinieFactura> linii,
                                          Currency moneda) {
        if (serie == null || serie.trim().isEmpty()) {
            throw new DomainValidationException("SERIE_LIPSA", "Seria facturii este obligatorie!");
        }
        if (numar == null || numar.trim().isEmpty()) {
            throw new DomainValidationException("NUMAR_LIPSA", "Numarul facturii este obligatoriu!");
        }
        if (dataEmitere == null) {
            throw new DomainValidationException("DATA_EMITERE_LIPSA", "Data emiterii facturii este obligatorie!");
        }
        if (dataScadenta == null) {
            throw new DomainValidationException("DATA_SCADENTA_LIPSA", "Data scadenta a facturii este obligatorie!");
        }
        if (dataScadenta.isBefore(dataEmitere)) {
            throw new DomainValidationException("SCADENTA_INVALIDA", "Data scadenta nu poate fi anterioara datei de emitere!");
        }
        if (comandaId == null) {
            throw new DomainValidationException("COMANDA_LIPSA", "Factura trebuie asociata unei comenzi de aprovizionare!");
        }
        if (furnizorId == null) {
            throw new DomainValidationException("FURNIZOR_LIPSA", "Factura trebuie asociata unui furnizor!");
        }
        if (linii == null || linii.isEmpty()) {
            throw new DomainValidationException("LINII_LIPSA", "Factura trebuie sa contina cel putin o linie de factura!");
        }
        if (moneda == null) {
            moneda = Currency.getInstance("RON");
        }

        UUID idFactura = UUID.randomUUID();
        return new FacturaFurnizor(idFactura, serie.trim(), numar.trim(), dataEmitere, dataScadenta, comandaId, furnizorId, linii, moneda);
    }
}
