package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "distributed_locks")
public class DistributedLockEntity implements Serializable {
    private static final long serialVersionUID = 1L;


    @Id
    @Column(name = "lock_name", nullable = false, length = 100)
    private String lockName;

    @Column(name = "locked_until", nullable = false)
    private Instant lockedUntil;

    @Column(name = "locked_by", nullable = false, length = 100)
    private String lockedBy;

    protected DistributedLockEntity() {}

    public DistributedLockEntity(String lockName, Instant lockedUntil, String lockedBy) {
        this.lockName = lockName;
        this.lockedUntil = lockedUntil;
        this.lockedBy = lockedBy;
    }

    public String getLockName() {
        return lockName;
    }

    public Instant getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(Instant lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }
}

