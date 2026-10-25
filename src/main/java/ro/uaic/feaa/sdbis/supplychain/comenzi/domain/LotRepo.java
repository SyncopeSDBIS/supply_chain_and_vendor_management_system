package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import java.util.List;
import java.util.Optional;

/**
 * Repository read-only pentru Loturi (Figura 10). Mutațiile trec exclusiv prin ComandaAprovizionare.
 */
public interface LotRepo {
    Optional<Lot> findById(IdLot id);
    List<Lot> findAllByComandaId(IdComanda id);
}
