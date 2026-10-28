package ro.uaic.feaa.sdbis.supplychain.comenzi.domain.factory;

import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.LinieComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Factory conform Sprint 2 (ADR-003).
 * Garanteaza initializarea consistenta a agregatului ComandaAprovizionare.
 */
@DomainFactory
public class ComandaFactory {

    public static ComandaAprovizionare creeaza(String numarComanda,
                                               IdFurnizor furnizorId,
                                               UUID cumparatorId,
                                               LocalDate dataComanda,
                                               LocalDate dataLivrareCeruta,
                                               String depozit,
                                               List<LinieComanda> linii) {
        Objects.requireNonNull(numarComanda, "numarComanda cannot be null");
        Objects.requireNonNull(furnizorId, "furnizorId cannot be null");
        Objects.requireNonNull(cumparatorId, "cumparatorId cannot be null");
        Objects.requireNonNull(dataComanda, "dataComanda cannot be null");
        Objects.requireNonNull(dataLivrareCeruta, "dataLivrareCeruta cannot be null");
        Objects.requireNonNull(depozit, "depozit cannot be null");
        if (linii == null || linii.isEmpty()) {
            throw new IllegalArgumentException("O comanda trebuie sa aiba cel putin o linie!");
        }
        if (dataLivrareCeruta.isBefore(dataComanda)) {
            throw new IllegalArgumentException("Data de livrare ceruta nu poate fi anterioara datei comenzii!");
        }

        IdComanda id = IdComanda.random();
        ComandaAprovizionare comanda = new ComandaAprovizionare(
            id, numarComanda, furnizorId, cumparatorId, dataComanda, dataLivrareCeruta, depozit, linii
        );
        comanda.trimiteLaFurnizor();
        return comanda;
    }
}
