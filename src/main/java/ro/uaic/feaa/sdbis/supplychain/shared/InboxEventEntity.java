package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shared_inbox")
public class InboxEventEntity implements Serializable {

    @Id
    @Column(length = 128)
    private String id; // format: "consumer:eventId"

    @Column(nullable = false)
    private UUID eventId;

    @Column(nullable = false)
    private String consumer;

    @Column(nullable = false)
    private Instant processedAt;

    protected InboxEventEntity() {}

    public InboxEventEntity(String consumer, UUID eventId, Instant processedAt) {
        this.consumer = consumer != null ? consumer : "default";
        this.eventId = eventId;
        this.id = this.consumer + ":" + eventId;
        this.processedAt = processedAt;
    }

    public String getId() { return id; }
    public UUID getEventId() { return eventId; }
    public String getConsumer() { return consumer; }
    public Instant getProcessedAt() { return processedAt; }
}
