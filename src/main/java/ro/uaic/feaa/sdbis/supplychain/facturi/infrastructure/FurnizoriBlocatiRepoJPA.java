package ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FurnizoriBlocatiRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FurnizoriBlocatiRepoJPA implements FurnizoriBlocatiRepo {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void adauga(IdFurnizor id) {
        if (id == null || id.getValue() == null) return;
        FurnizorBlocatFactura existing = entityManager.find(FurnizorBlocatFactura.class, id.getValue());
        if (existing == null) {
            entityManager.persist(new FurnizorBlocatFactura(id.getValue()));
            entityManager.flush();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean esteBlocat(IdFurnizor id) {
        if (id == null || id.getValue() == null) return false;
        return entityManager.find(FurnizorBlocatFactura.class, id.getValue()) != null;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<IdFurnizor> obtineTotiFurnizoriiBlocati() {
        List<UUID> ids = entityManager.createQuery(
                "SELECT f.furnizorId FROM FurnizorBlocatFactura f", UUID.class)
                .getResultList();
        return ids.stream().map(IdFurnizor::of).collect(Collectors.toSet());
    }
}
