package ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure;

import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.OrdinPlata;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.OrdinPlataRepo;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareOrdinPlata;
import ro.uaic.feaa.sdbis.supplychain.shared.AbstractRepositoryJPA;

import java.util.List;
import java.util.UUID;

@Repository
public class OrdinPlataRepoJPA extends AbstractRepositoryJPA<OrdinPlata, UUID> implements OrdinPlataRepo {
    public OrdinPlataRepoJPA() {
        super(OrdinPlata.class);
    }

    @Override
    public List<OrdinPlata> findOrdinePlata(UUID facturaId, StareOrdinPlata stare, int page, int size) {
        StringBuilder jpql = new StringBuilder("SELECT o FROM OrdinPlata o WHERE 1=1");
        if (facturaId != null) {
            jpql.append(" AND o.facturaId = :fId");
        }
        if (stare != null) {
            jpql.append(" AND o.stareOrdin = :stare");
        }

        var query = em.createQuery(jpql.toString(), OrdinPlata.class);
        if (facturaId != null) {
            query.setParameter("fId", facturaId);
        }
        if (stare != null) {
            query.setParameter("stare", stare);
        }
        query.setFirstResult(Math.max(0, page) * Math.max(1, size));
        query.setMaxResults(Math.max(1, size));
        return query.getResultList();
    }
}
