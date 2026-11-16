package ro.uaic.feaa.sdbis.supplychain.shared;

import java.time.Duration;

/**
 * Interfata pentru obtinerea de blocari distribuite la nivel de baza de date (ADR-004),
 * asigurand rularea idempotent-exclusiva a task-urilor planificate in cluster.
 */
public interface DistributedLockService {
    boolean tryLock(String lockName, Duration duration);
    void releaseLock(String lockName);
}

