package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "shared_audit_log")
public class AuditLogEntry implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "actiune", nullable = false, length = 255)
    private String actiune;

    @Column(name = "obiect", columnDefinition = "TEXT", nullable = false)
    private String obiect;

    protected AuditLogEntry() {}

    public AuditLogEntry(UUID id, Instant timestamp, String actiune, String obiect) {
        this.id = Objects.requireNonNull(id);
        this.timestamp = Objects.requireNonNull(timestamp);
        this.actiune = Objects.requireNonNull(actiune);
        this.obiect = Objects.requireNonNull(obiect);
    }

    public UUID getId() { return id; }
    public Instant getTimestamp() { return timestamp; }
    public String getActiune() { return actiune; }
    public String getObiect() { return obiect; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuditLogEntry that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
