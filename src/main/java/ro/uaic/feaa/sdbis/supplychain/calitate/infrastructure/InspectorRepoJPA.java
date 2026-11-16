package ro.uaic.feaa.sdbis.supplychain.calitate.infrastructure;

import org.springframework.stereotype.Repository;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectorCalitate;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectorRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.AbstractRepositoryJPA;

import java.util.List;
import java.util.UUID;

@Repository
public class InspectorRepoJPA extends AbstractRepositoryJPA<InspectorCalitate, UUID> implements InspectorRepo {
    public InspectorRepoJPA() {
        super(InspectorCalitate.class);
    }

    @Override
    public List<InspectorCalitate> findAllWithDetails() {
        List<InspectorCalitate> list = em.createQuery(
                "SELECT DISTINCT i FROM InspectorCalitate i LEFT JOIN FETCH i.certificari",
                InspectorCalitate.class
        ).getResultList();

        if (!list.isEmpty()) {
            em.createQuery("SELECT DISTINCT i FROM InspectorCalitate i LEFT JOIN FETCH i.disponibilitate WHERE i IN :inspectori", InspectorCalitate.class)
              .setParameter("inspectori", list)
              .getResultList();
        }
        return list;
    }
}
