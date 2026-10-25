package ro.uaic.feaa.sdbis.supplychain.infrastructure.configuration;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

/**
 * Entity Listener pentru audit si reguli de lifecycle conform Sprint 3.
 */
public class AggregateAuditAndComputationListener {

    @PrePersist
    public void onPrePersist(Object entity) {
        // Lifecycle Hook
    }

    @PreUpdate
    public void onPreUpdate(Object entity) {
        // Lifecycle Hook
    }

    @PreRemove
    public void onPreRemove(Object entity) {
        // Lifecycle Hook
    }
}
