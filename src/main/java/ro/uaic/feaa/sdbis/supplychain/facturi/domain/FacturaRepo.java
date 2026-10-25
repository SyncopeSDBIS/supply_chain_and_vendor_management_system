package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FacturaRepo {
    void add(FacturaFurnizor f);
    Optional<FacturaFurnizor> findById(UUID id);
    void save(FacturaFurnizor f);
    List<FacturaFurnizor> findAll();
}
