package ro.uaic.feaa.sdbis.supplychain.infrastructure.persistence.springdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareFactura;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataFacturaRepository extends JpaRepository<FacturaFurnizor, UUID> {
    List<FacturaFurnizor> findByComandaId(IdComanda comandaId);
    List<FacturaFurnizor> findByStareFactura(StareFactura stareFactura);
}
