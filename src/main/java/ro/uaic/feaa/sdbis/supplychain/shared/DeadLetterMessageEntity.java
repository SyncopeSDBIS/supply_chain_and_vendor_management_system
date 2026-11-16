package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Enterprise AMQP Dead-Letter Message entity for poison pill persistence and re-drive (AMQP-AUDIT-06).
 */
@Entity
@Table(name = "shared_dlq_parking_lot")
public class DeadLetterMessageEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "consumer_id", length = 100)
    private String consumerId;

    @Column(name = "routing_key", length = 255)
    private String routingKey;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "failed_at", nullable = false)
    private Instant failedAt;

    @Column(name = "retried", nullable = false)
    private boolean retried = false;

    @Column(name = "retried_at")
    private Instant retriedAt;

    protected DeadLetterMessageEntity() {}

    public DeadLetterMessageEntity(UUID id, String consumerId, String routingKey, String payload, String errorMessage, Instant failedAt) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.consumerId = consumerId;
        this.routingKey = routingKey;
        this.payload = payload;
        this.errorMessage = errorMessage;
        this.failedAt = failedAt != null ? failedAt : Instant.now();
        this.retried = false;
    }

    public UUID getId() {
        return id;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public String getPayload() {
        return payload;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Instant getFailedAt() {
        return failedAt;
    }

    public boolean isRetried() {
        return retried;
    }

    public Instant getRetriedAt() {
        return retriedAt;
    }

    public void markRetried() {
        this.retried = true;
        this.retriedAt = Instant.now();
    }
}
