package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DomainRepository
public interface FacturaRepo {
    void add(FacturaFurnizor f);
    Optional<FacturaFurnizor> findById(UUID id);
    void save(FacturaFurnizor f);
    List<FacturaFurnizor> findAll();
}
