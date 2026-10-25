package ro.uaic.feaa.sdbis.supplychain.shared;

import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.UUID;

@Repository
public class InboxRepoJPA extends AbstractRepositoryJPA<InboxEventEntity, String> implements InboxRepo {

    public InboxRepoJPA() {
        super(InboxEventEntity.class);
    }

    @Override
    public boolean existaEveniment(UUID eventId) {
        return existaEveniment("default", eventId);
    }

    @Override
    public void marcheazaProcesat(UUID eventId) {
        marcheazaProcesat("default", eventId);
    }

    @Override
    public boolean existaEveniment(String consumer, UUID eventId) {
        String key = (consumer != null ? consumer : "default") + ":" + eventId;
        return em.find(InboxEventEntity.class, key) != null;
    }

    @Override
    public void marcheazaProcesat(String consumer, UUID eventId) {
        if (!existaEveniment(consumer, eventId)) {
            em.persist(new InboxEventEntity(consumer, eventId, Instant.now()));
            em.flush();
        }
    }
}
