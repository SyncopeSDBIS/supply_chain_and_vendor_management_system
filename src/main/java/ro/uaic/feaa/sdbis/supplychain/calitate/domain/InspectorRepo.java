package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InspectorRepo {
    void add(InspectorCalitate i);
    Optional<InspectorCalitate> findById(UUID id);
    void save(InspectorCalitate i);
    List<InspectorCalitate> findAll();
}
