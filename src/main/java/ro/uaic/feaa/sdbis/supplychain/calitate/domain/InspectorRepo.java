package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DomainRepository
public interface InspectorRepo {
    void add(InspectorCalitate i);
    Optional<InspectorCalitate> findById(UUID id);
    void save(InspectorCalitate i);
    List<InspectorCalitate> findAll();
}
