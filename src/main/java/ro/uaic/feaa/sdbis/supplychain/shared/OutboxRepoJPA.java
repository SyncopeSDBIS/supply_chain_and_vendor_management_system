package ro.uaic.feaa.sdbis.supplychain.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class OutboxRepoJPA extends AbstractRepositoryJPA<OutboxEventEntity, UUID> implements OutboxRepo {

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
    public void saveEvent(DomainEvent e) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(e);
        } catch (Exception ex) {
            payload = "{\"error\":\"SerializationFailed\",\"message\":\"" + ex.getMessage() + "\"}";
        }

        OutboxEventEntity entity = new OutboxEventEntity(
            e.eventId(), e.occurredOn() != null ? e.occurredOn() : Instant.now(), e.getClass().getName(), payload
        );
        em.persist(entity);
        em.flush();
    }

    @Override
    public List<DomainEvent> pollUnpublishedEvents() {
        List<OutboxEventEntity> list = em.createQuery(
            "SELECT o FROM OutboxEventEntity o WHERE o.published = false ORDER BY o.occurredOn ASC",
            OutboxEventEntity.class
        ).getResultList();

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
                } catch (Exception ignored) {
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
    public void markAsPublished(UUID eventId) {
        OutboxEventEntity entity = em.find(OutboxEventEntity.class, eventId);
        if (entity != null) {
            entity.setPublished(true);
            em.merge(entity);
            em.flush();
        }
    }
}
