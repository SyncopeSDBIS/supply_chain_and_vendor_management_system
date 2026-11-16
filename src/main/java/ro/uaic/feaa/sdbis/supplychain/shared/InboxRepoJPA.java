package ro.uaic.feaa.sdbis.supplychain.shared;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Repository
public class InboxRepoJPA extends AbstractRepositoryJPA<InboxEventEntity, String> implements InboxRepo {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    public InboxRepoJPA() {
        super(InboxEventEntity.class);
    }

    @Override
    public boolean existaEveniment(UUID eventId) {
        return existaEveniment("default", eventId);
    }

    @Override
    @Transactional
    public void marcheazaProcesat(UUID eventId) {
        marcheazaProcesat("default", eventId);
    }

    @Override
    public boolean existaEveniment(String consumer, UUID eventId) {
        String key = (consumer != null ? consumer : "default") + ":" + eventId;
        return em.find(InboxEventEntity.class, key) != null;
    }

    @Override
    @Transactional
    public void marcheazaProcesat(String consumer, UUID eventId) {
        incearcaInregistrare(consumer, eventId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean incearcaInregistrare(UUID eventId) {
        return incearcaInregistrare("default", eventId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean incearcaInregistrare(String consumer, UUID eventId) {
        String consumerKey = consumer != null ? consumer : "default";
        String key = consumerKey + ":" + eventId;

        if (jdbcTemplate != null) {
            try {
                int rows = jdbcTemplate.update(
                        "INSERT INTO shared_inbox (id, event_id, consumer, processed_at) VALUES (?, ?, ?, ?)",
                        key,
                        eventId,
                        consumerKey,
                        Timestamp.from(Instant.now())
                );
                return rows > 0;
            } catch (DataIntegrityViolationException ex) {
                return false;
            }
        }

        if (em.find(InboxEventEntity.class, key) != null) {
            return false;
        }
        try {
            em.persist(new InboxEventEntity(consumerKey, eventId, Instant.now()));
            em.flush();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int purgeProcessedEventsOlderThan(Instant cutoff) {
        return em.createQuery("DELETE FROM InboxEventEntity i WHERE i.processedAt < :cutoff")
            .setParameter("cutoff", cutoff)
            .executeUpdate();
    }
}
