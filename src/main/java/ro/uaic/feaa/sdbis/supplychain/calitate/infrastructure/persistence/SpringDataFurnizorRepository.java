package ro.uaic.feaa.sdbis.supplychain.calitate.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Furnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.StareFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataFurnizorRepository extends JpaRepository<Furnizor, IdFurnizor> {
    Optional<Furnizor> findByCodFiscal(String codFiscal);
    List<Furnizor> findByStareFurnizor(StareFurnizor stareFurnizor);

    @Query("SELECT f FROM Furnizor f WHERE f.stareFurnizor = :stare ORDER BY f.denumire ASC")
    List<Furnizor> findActiveFurnizoriOrdered(@Param("stare") StareFurnizor stare);
}
