package ro.uaic.feaa.sdbis.supplychain.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Repository
public class OutboxRepoJPA extends AbstractRepositoryJPA<OutboxEventEntity, UUID> implements OutboxRepo {

    private final ObjectMapper objectMapper;

    public OutboxRepoJPA(@Autowired(required = false) ObjectMapper objectMapper) {
        super(OutboxEventEntity.class);
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
    }

    @Override
    public void saveEvent(DomainEvent e) {
        String payload;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(e);
            payload = "B64:" + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception ex) {
            try {
                payload = objectMapper.writeValueAsString(e);
            } catch (Exception ignored) {
                payload = e.toString();
            }
        }

        OutboxEventEntity entity = new OutboxEventEntity(
            e.eventId(), e.occurredOn(), e.getClass().getName(), payload
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
            if (entity.getPayload() != null && entity.getPayload().startsWith("B64:")) {
                try {
                    byte[] data = Base64.getDecoder().decode(entity.getPayload().substring(4));
                    try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
                         ObjectInputStream ois = new ObjectInputStream(bais)) {
                        Object obj = ois.readObject();
                        if (obj instanceof DomainEvent d) {
                            de = d;
                        }
                    }
                } catch (Exception ignored) {
                }
            }

            if (de == null) {
                try {
                    Class<?> clazz = Class.forName(entity.getEventType());
                    Object obj = objectMapper.readValue(entity.getPayload(), clazz);
                    if (obj instanceof DomainEvent d) {
                        de = d;
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
