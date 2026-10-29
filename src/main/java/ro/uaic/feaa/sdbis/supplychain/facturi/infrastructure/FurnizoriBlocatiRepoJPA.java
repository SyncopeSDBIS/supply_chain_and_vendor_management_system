package ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FurnizoriBlocatiRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

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
}
