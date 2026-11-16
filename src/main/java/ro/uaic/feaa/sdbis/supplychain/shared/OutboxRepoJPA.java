package ro.uaic.feaa.sdbis.supplychain.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class OutboxRepoJPA extends AbstractRepositoryJPA<OutboxEventEntity, UUID> implements OutboxRepo {

    private static final Logger log = LoggerFactory.getLogger(OutboxRepoJPA.class);

    private final ObjectMapper objectMapper;

    public OutboxRepoJPA(@Autowired(required = false) ObjectMapper objectMapper) {
        super(OutboxEventEntity.class);
        if (objectMapper != null) {
            this.objectMapper = objectMapper;
        } else {
            this.objectMapper = new ObjectMapper();
            this.objectMapper.registerModule(new JavaTimeModule());
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void saveEvent(DomainEvent e) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(e);
        } catch (Exception ex) {
            throw new IllegalStateException("Eroare serializare eveniment outbox: " + e.getClass().getName(), ex);
        }

        OutboxEventEntity entity = new OutboxEventEntity(
            e.eventId(), e.occurredOn() != null ? e.occurredOn() : Instant.now(), e.getClass().getName(), payload
        );
        em.persist(entity);
    }

    @Override
    @Transactional
    public List<DomainEvent> pollUnpublishedEvents() {
        List<OutboxEventEntity> list = em.createQuery(
            "SELECT o FROM OutboxEventEntity o WHERE o.published = false ORDER BY o.occurredOn ASC",
            OutboxEventEntity.class
        )
        .setMaxResults(100)
        .getResultList();

        List<DomainEvent> events = new ArrayList<>();
        for (OutboxEventEntity entity : list) {
            DomainEvent de = null;
            if (entity.getPayload() != null && !entity.getPayload().isBlank()) {
                try {
                    Class<?> clazz = Class.forName(entity.getEventType());
                    if (DomainEvent.class.isAssignableFrom(clazz)) {
                        Object obj = objectMapper.readValue(entity.getPayload(), clazz);
                        if (obj instanceof DomainEvent d) {
                            de = d;
                        }
                    }
                } catch (Exception ex) {
                    log.warn("[Outbox] Eroare la deserializarea evenimentului {} (tip: {}): {}",
                            entity.getEventId(), entity.getEventType(), ex.getMessage());
                }
            }

            if (de == null) {
                de = new StoredDomainEvent(entity.getEventId(), entity.getOccurredOn(), entity.getEventType(), entity.getPayload());
            }
            events.add(de);
        }
        return events;
    }

    @Override
    @Transactional
    public void markAsPublished(UUID eventId) {
        OutboxEventEntity entity = em.find(OutboxEventEntity.class, eventId);
        if (entity != null) {
            entity.setPublished(true);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int purgePublishedEventsOlderThan(Instant cutoff) {
        return em.createQuery("DELETE FROM OutboxEventEntity o WHERE o.published = true AND o.occurredOn < :cutoff")
            .setParameter("cutoff", cutoff)
            .executeUpdate();
    }
}
