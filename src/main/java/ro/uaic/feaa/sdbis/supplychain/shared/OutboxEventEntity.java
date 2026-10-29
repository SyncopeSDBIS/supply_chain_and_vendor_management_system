package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "shared_outbox")
public class OutboxEventEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private UUID eventId;

    @Column(name = "timestamp", nullable = false)
    private Instant occurredOn;

    @Column(name = "event_type", nullable = false, length = 255)
    private String eventType;

    @Column(name = "payload", columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Column(name = "published", nullable = false)
    private boolean published = false;

    protected OutboxEventEntity() {}

    public OutboxEventEntity(UUID eventId, Instant occurredOn, String eventType, String payload) {
        this.eventId = Objects.requireNonNull(eventId);
        this.occurredOn = Objects.requireNonNull(occurredOn);
        this.eventType = Objects.requireNonNull(eventType);
        this.payload = Objects.requireNonNull(payload);
        this.published = false;
    }

    public UUID getEventId() { return eventId; }
    public Instant getOccurredOn() { return occurredOn; }
    public String getEventType() { return eventType; }
    public String getPayload() { return payload; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OutboxEventEntity that)) return false;
        return eventId != null && Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
