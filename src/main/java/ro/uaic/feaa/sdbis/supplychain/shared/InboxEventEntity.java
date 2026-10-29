package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "shared_inbox")
public class InboxEventEntity implements Serializable {

    @Id
    @Column(name = "id", length = 128)
    private String id; // format: "consumer:eventId"

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "consumer", nullable = false, length = 255)
    private String consumer;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    protected InboxEventEntity() {}

    public InboxEventEntity(String consumer, UUID eventId, Instant processedAt) {
        this.consumer = consumer != null ? consumer : "default";
        this.eventId = Objects.requireNonNull(eventId);
        this.id = this.consumer + ":" + eventId;
        this.processedAt = Objects.requireNonNull(processedAt);
    }

    public String getId() { return id; }
    public UUID getEventId() { return eventId; }
    public String getConsumer() { return consumer; }
    public Instant getProcessedAt() { return processedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InboxEventEntity that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
