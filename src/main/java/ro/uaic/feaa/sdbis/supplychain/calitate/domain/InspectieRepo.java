package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InspectieRepo {
    void add(InspectieCalitate i);
    Optional<InspectieCalitate> findById(UUID id);
    void save(InspectieCalitate i);
    List<InspectieCalitate> findAll();
}
