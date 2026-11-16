package ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareFactura;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository pentru agregatul FacturaFurnizor (Sprint 3 - Cerinta Standard).
 */
@Repository
public interface SpringDataFacturaRepository extends JpaRepository<FacturaFurnizor, UUID> {

    Optional<FacturaFurnizor> findBySerieAndNumar(String serie, String numar);

    List<FacturaFurnizor> findByComandaId(IdComanda comandaId);

    List<FacturaFurnizor> findByFurnizorId(IdFurnizor furnizorId);

    List<FacturaFurnizor> findByStareFactura(StareFactura stareFactura);

    @Query("SELECT f FROM FacturaFurnizor f WHERE f.furnizorId = :furnizorId AND f.stareFactura IN ('AprobataLaPlata', 'PartialPlatita')")
    List<FacturaFurnizor> findFacturiNeachitatePentruFurnizor(@Param("furnizorId") IdFurnizor furnizorId);
}
