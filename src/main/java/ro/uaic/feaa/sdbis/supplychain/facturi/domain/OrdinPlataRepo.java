package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DomainRepository
public interface OrdinPlataRepo {
    void add(OrdinPlata o);
    Optional<OrdinPlata> findById(UUID id);
    void save(OrdinPlata o);
    List<OrdinPlata> findAll();
}
