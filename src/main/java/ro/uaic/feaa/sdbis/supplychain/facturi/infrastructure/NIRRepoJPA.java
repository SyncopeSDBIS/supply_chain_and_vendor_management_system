package ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure;

import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.NIR;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.NIRRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.AbstractRepositoryJPA;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import java.util.List;
import java.util.UUID;

@Repository
public class NIRRepoJPA extends AbstractRepositoryJPA<NIR, UUID> implements NIRRepo {

    public NIRRepoJPA() {
        super(NIR.class);
    }

    @Override
    public List<NIR> findByComandaId(IdComanda id) {
        List<NIR> nirs = em.createQuery("SELECT n FROM NIR n WHERE n.comandaId = :cid", NIR.class)
            .setParameter("cid", id)
            .getResultList();
        if (!nirs.isEmpty()) {
            em.createQuery("SELECT DISTINCT n FROM NIR n LEFT JOIN FETCH n.linii WHERE n IN :nirs", NIR.class)
              .setParameter("nirs", nirs)
              .getResultList();
        }
        return nirs;
    }

    @Override
    public boolean existaNIRPentruEvent(UUID eventId) {
        Long count = em.createQuery("SELECT COUNT(n) FROM NIR n WHERE n.eventSursaId = :eid", Long.class)
            .setParameter("eid", eventId)
            .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public List<NIR> findNIR(IdComanda comandaId, int page, int size) {
        StringBuilder jpql = new StringBuilder("SELECT n FROM NIR n");
        if (comandaId != null) {
            jpql.append(" WHERE n.comandaId = :cid");
        }
        jpql.append(" ORDER BY n.dataReceptie DESC");

        var query = em.createQuery(jpql.toString(), NIR.class);
        if (comandaId != null) {
            query.setParameter("cid", comandaId);
        }
        query.setFirstResult(Math.max(0, page) * Math.max(1, size));
        query.setMaxResults(Math.max(1, size));
        List<NIR> nirs = query.getResultList();

        // FIX JPA-01: Two-step fetch pentru eliminarea N+1 pe linii_nir
        if (!nirs.isEmpty()) {
            em.createQuery("SELECT DISTINCT n FROM NIR n LEFT JOIN FETCH n.linii WHERE n IN :nirs", NIR.class)
              .setParameter("nirs", nirs)
              .getResultList();
        }
        return nirs;
    }
}
