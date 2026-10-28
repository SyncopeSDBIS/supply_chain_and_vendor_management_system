package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DomainRepository
public interface NIRRepo {
    void add(NIR n);
    Optional<NIR> findById(UUID id);
    void save(NIR n);
    List<NIR> findByComandaId(IdComanda id);
    boolean existaNIRPentruEvent(UUID eventId);
    List<NIR> findAll();
}
