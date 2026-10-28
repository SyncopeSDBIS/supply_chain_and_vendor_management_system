package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DomainRepository
public interface NCRRepo {
    void add(RaportNeconformitate r);
    Optional<RaportNeconformitate> findById(UUID id);
    void save(RaportNeconformitate r);
    List<RaportNeconformitate> findAll();
}
