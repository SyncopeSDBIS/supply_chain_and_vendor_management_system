package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NCRRepo {
    void add(RaportNeconformitate r);
    Optional<RaportNeconformitate> findById(UUID id);
    void save(RaportNeconformitate r);
    List<RaportNeconformitate> findAll();
}
