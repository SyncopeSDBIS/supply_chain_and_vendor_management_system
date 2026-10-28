package ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareFactura;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataFacturaRepository extends JpaRepository<FacturaFurnizor, UUID> {
    Optional<FacturaFurnizor> findBySerieAndNumar(String serie, String numar);
    List<FacturaFurnizor> findByStareFactura(StareFactura stareFactura);
}
