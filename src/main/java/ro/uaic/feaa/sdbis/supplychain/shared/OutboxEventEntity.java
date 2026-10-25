package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shared_outbox")
public class OutboxEventEntity implements Serializable {

    @Id
    private UUID eventId;

    @Column(nullable = false)
    private Instant occurredOn;

    @Column(nullable = false)
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private boolean published = false;

    protected OutboxEventEntity() {}

    public OutboxEventEntity(UUID eventId, Instant occurredOn, String eventType, String payload) {
        this.eventId = eventId;
        this.occurredOn = occurredOn;
        this.eventType = eventType;
        this.payload = payload;
        this.published = false;
    }

    public UUID getEventId() { return eventId; }
    public Instant getOccurredOn() { return occurredOn; }
    public String getEventType() { return eventType; }
    public String getPayload() { return payload; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
}
