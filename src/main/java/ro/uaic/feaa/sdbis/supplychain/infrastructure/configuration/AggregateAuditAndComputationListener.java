package ro.uaic.feaa.sdbis.supplychain.infrastructure.configuration;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

// Global JPA Entity Listener pentru audit si reguli de lifecycle conform Sprint 3.
// Inregistrat prin META-INF/orm.xml pentru toate entitatile din persistence unit.
public class AggregateAuditAndComputationListener {

    private static final Logger log = LoggerFactory.getLogger(AggregateAuditAndComputationListener.class);
    private static final AtomicLong persistCounter = new AtomicLong(0);
    private static final AtomicLong updateCounter = new AtomicLong(0);
    private static final AtomicLong removeCounter = new AtomicLong(0);

    @PrePersist
    public void onPrePersist(Object entity) {
        long count = persistCounter.incrementAndGet();
        log.info("[JPA Lifecycle #{} - PrePersist] Entitate persistata: {}", count, entity.getClass().getName());
    }

    @PreUpdate
    public void onPreUpdate(Object entity) {
        long count = updateCounter.incrementAndGet();
        log.info("[JPA Lifecycle #{} - PreUpdate] Entitate modificata: {}", count, entity.getClass().getName());
    }

    @PreRemove
    public void onPreRemove(Object entity) {
        long count = removeCounter.incrementAndGet();
        log.warn("[JPA Lifecycle #{} - PreRemove] Entitate stearsa: {}", count, entity.getClass().getName());
    }

    public static long getPersistCount() {
        return persistCounter.get();
    }

    public static long getUpdateCount() {
        return updateCounter.get();
    }

    public static long getRemoveCount() {
        return removeCounter.get();
    }

    public static void resetCounters() {
        persistCounter.set(0);
        updateCounter.set(0);
        removeCounter.set(0);
    }
}
