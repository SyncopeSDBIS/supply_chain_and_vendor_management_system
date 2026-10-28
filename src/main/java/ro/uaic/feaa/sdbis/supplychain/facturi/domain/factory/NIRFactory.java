package ro.uaic.feaa.sdbis.supplychain.facturi.domain.factory;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.LinieNIR;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.NIR;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainFactory;
import ro.uaic.feaa.sdbis.supplychain.shared.exception.DomainValidationException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Factory pentru NIR (Nota de Intrare-Receptie) (Sprint 2 - ADR-003).
 * Garanteaza crearea conforma si idempotenta a receptiei de marfa.
 */
@DomainFactory
public class NIRFactory {

    public static NIR creeaza(String numarNIR,
                             IdComanda comandaId,
                             UUID eventSursaId,
                             LocalDate dataReceptie,
                             List<LinieNIR> linii) {
        if (numarNIR == null || numarNIR.trim().isEmpty()) {
            throw new DomainValidationException("NUMAR_NIR_LIPSA", "Numarul NIR-ului este obligatoriu!");
        }
        if (comandaId == null) {
            throw new DomainValidationException("COMANDA_LIPSA", "NIR-ul trebuie asociat unei comenzi!");
        }
        if (eventSursaId == null) {
            throw new DomainValidationException("EVENT_SURSA_LIPSA", "Identificatorul evenimentului sursa este obligatoriu pentru idempotenta!");
        }
        if (dataReceptie == null) {
            throw new DomainValidationException("DATA_RECEPTIE_LIPSA", "Data receptiei este obligatorie!");
        }
        if (linii == null || linii.isEmpty()) {
            throw new DomainValidationException("LINII_NIR_LIPSA", "NIR-ul trebuie sa contina cel putin o linie de receptie!");
        }

        UUID idNIR = UUID.randomUUID();
        return new NIR(idNIR, numarNIR.trim(), comandaId, eventSursaId, dataReceptie, linii);
    }
}
