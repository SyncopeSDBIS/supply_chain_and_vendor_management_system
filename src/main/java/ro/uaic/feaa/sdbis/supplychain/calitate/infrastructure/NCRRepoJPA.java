package ro.uaic.feaa.sdbis.supplychain.calitate.infrastructure;

import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.NCRRepo;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.RaportNeconformitate;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.StareNCR;
import ro.uaic.feaa.sdbis.supplychain.shared.AbstractRepositoryJPA;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

import java.util.List;
import java.util.UUID;

@Repository
public class NCRRepoJPA extends AbstractRepositoryJPA<RaportNeconformitate, UUID> implements NCRRepo {
    public NCRRepoJPA() {
        super(RaportNeconformitate.class);
    }

    @Override
    public List<RaportNeconformitate> findNCR(IdFurnizor furnizorId, StareNCR stare, int page, int size) {
        StringBuilder jpql = new StringBuilder("SELECT n FROM RaportNeconformitate n WHERE 1=1");
        if (furnizorId != null) {
            jpql.append(" AND n.furnizorId = :fId");
        }
        if (stare != null) {
            jpql.append(" AND n.stareNCR = :stare");
        }

        var query = em.createQuery(jpql.toString(), RaportNeconformitate.class);
        if (furnizorId != null) {
            query.setParameter("fId", furnizorId);
        }
        if (stare != null) {
            query.setParameter("stare", stare);
        }
        query.setFirstResult(Math.max(0, page) * Math.max(1, size));
        query.setMaxResults(Math.max(1, size));
        return query.getResultList();
    }

    @Override
    public List<RaportNeconformitate> findByFurnizorIds(List<IdFurnizor> furnizorIds) {
        if (furnizorIds == null || furnizorIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return em.createQuery("SELECT n FROM RaportNeconformitate n WHERE n.furnizorId IN (:fIds)", RaportNeconformitate.class)
            .setParameter("fIds", furnizorIds)
            .getResultList();
    }
}
