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
        return em.createQuery("SELECT n FROM NIR n WHERE n.comandaId = :cid", NIR.class)
            .setParameter("cid", id)
            .getResultList();
    }

    @Override
    public boolean existaNIRPentruEvent(UUID eventId) {
        Long count = em.createQuery("SELECT COUNT(n) FROM NIR n WHERE n.eventSursaId = :eid", Long.class)
            .setParameter("eid", eventId)
            .getSingleResult();
        return count != null && count > 0;
    }
}
