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
 * Entitate JPA pentru stocarea pe termen lung a inregistrarilor de audit arhivate (cold-storage, OPS-01).
 * Folosita pentru prevenirea degradarii de performanta I/O pe tabela tranzactionala activa shared_audit_log.
 */
@Entity
@Table(name = "shared_audit_log_archive")
public class AuditLogArchiveEntry implements Serializable {
    private static final long serialVersionUID = 1L;


    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "actiune", nullable = false, length = 255)
    private String actiune;

    @Column(name = "obiect", columnDefinition = "TEXT", nullable = false)
    private String obiect;

    protected AuditLogArchiveEntry() {}

    public AuditLogArchiveEntry(UUID id, Instant timestamp, String actiune, String obiect) {
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
        if (!(o instanceof AuditLogArchiveEntry that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
