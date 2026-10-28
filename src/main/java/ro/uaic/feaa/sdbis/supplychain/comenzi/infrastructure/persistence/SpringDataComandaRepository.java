package ro.uaic.feaa.sdbis.supplychain.comenzi.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.StareComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataComandaRepository extends JpaRepository<ComandaAprovizionare, IdComanda> {
    Optional<ComandaAprovizionare> findByNumarComanda(String numarComanda);
    List<ComandaAprovizionare> findByStareComanda(StareComanda stareComanda);

    @Query("SELECT c FROM ComandaAprovizionare c WHERE c.furnizorId = :fid AND c.stareComanda = :stare")
    List<ComandaAprovizionare> findByFurnizorAndStare(@Param("fid") IdFurnizor furnizorId, @Param("stare") StareComanda stare);
}
