package ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.NIR;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareNIR;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository pentru agregatul NIR (Sprint 3 - Cerinta Standard).
 */
@Repository
public interface SpringDataNIRRepository extends JpaRepository<NIR, UUID> {

    Optional<NIR> findByNumarNIR(String numarNIR);

    List<NIR> findByComandaId(IdComanda comandaId);

    List<NIR> findByStareNIR(StareNIR stareNIR);

    boolean existsByEventSursaId(UUID eventSursaId);

    @Query("SELECT n FROM NIR n WHERE n.comandaId = :comandaId AND n.stareNIR = :stare")
    List<NIR> findByComandaAndStare(@Param("comandaId") IdComanda comandaId, @Param("stare") StareNIR stare);
}
