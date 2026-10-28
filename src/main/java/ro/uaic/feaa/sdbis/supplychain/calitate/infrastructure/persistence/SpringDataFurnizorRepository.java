package ro.uaic.feaa.sdbis.supplychain.calitate.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
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
}
