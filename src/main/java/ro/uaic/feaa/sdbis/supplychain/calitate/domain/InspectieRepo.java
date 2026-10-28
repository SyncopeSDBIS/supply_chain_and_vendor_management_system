package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DomainRepository
public interface InspectieRepo {
    void add(InspectieCalitate i);
    Optional<InspectieCalitate> findById(UUID id);
    void save(InspectieCalitate i);
    List<InspectieCalitate> findAll();
}
