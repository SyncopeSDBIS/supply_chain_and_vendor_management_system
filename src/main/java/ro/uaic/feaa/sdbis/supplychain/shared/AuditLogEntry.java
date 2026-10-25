package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shared_audit_log")
public class AuditLogEntry implements Serializable {

    @Id
    private UUID id;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false)
    private String actiune;

    @Column(nullable = false, length = 1000)
    private String obiect;

    protected AuditLogEntry() {}

    public AuditLogEntry(UUID id, Instant timestamp, String actiune, String obiect) {
        this.id = id;
        this.timestamp = timestamp;
        this.actiune = actiune;
        this.obiect = obiect;
    }

    public UUID getId() { return id; }
    public Instant getTimestamp() { return timestamp; }
    public String getActiune() { return actiune; }
    public String getObiect() { return obiect; }
}
