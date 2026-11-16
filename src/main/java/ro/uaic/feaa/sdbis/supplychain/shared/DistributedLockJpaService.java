package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Implementare JPA/JDBC pentru Distributed Locks ce previne rularea concurenta a scheduler-elor
 * in medii distribuite / multi-container (Docker Swarm, Kubernetes).
 * Foloseste JdbcTemplate pentru operatiuni de inserare atomica prevenind marcarea rollback-only in EntityManager.
 */
@Service
public class DistributedLockJpaService implements DistributedLockService {

    private static final Logger log = LoggerFactory.getLogger(DistributedLockJpaService.class);
    private final String nodeId = "node-" + UUID.randomUUID().toString().substring(0, 8);

    @PersistenceContext
    private EntityManager em;

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean tryLock(String lockName, Duration duration) {
        Instant now = Instant.now();
        Instant lockedUntil = now.plus(duration);

        try {
            // 1. Incearca sa reinnoiasca lock-ul daca a expirat
            int updated = em.createQuery(
                    "UPDATE DistributedLockEntity l SET l.lockedUntil = :newUntil, l.lockedBy = :nodeId " +
                    "WHERE l.lockName = :lockName AND l.lockedUntil < :now"
            )
            .setParameter("newUntil", lockedUntil)
            .setParameter("nodeId", nodeId)
            .setParameter("lockName", lockName)
            .setParameter("now", now)
            .executeUpdate();

            if (updated > 0) {
                log.debug("[DistributedLock] Lock '{}' reinnoit cu succes de nodul {}", lockName, nodeId);
                return true;
            }

            // 2. FIX CONC-03: Inserare atomica sigura via JdbcTemplate pentru a evita Rollback-Only pe EntityManager
            if (jdbcTemplate != null) {
                try {
                    int inserted = jdbcTemplate.update(
                            "INSERT INTO distributed_locks (lock_name, locked_until, locked_by) VALUES (?, ?, ?)",
                            lockName,
                            Timestamp.from(lockedUntil),
                            nodeId
                    );
                    if (inserted > 0) {
                        log.debug("[DistributedLock] Lock '{}' creat si ocupat atomic de nodul {}", lockName, nodeId);
                        return true;
                    }
                } catch (DataIntegrityViolationException ex) {
                    log.debug("[DistributedLock] Lock-ul '{}' este ocupat in prezent de alt nod (conflict concurent)", lockName);
                    return false;
                }
            } else {
                int inserted = em.createNativeQuery(
                        "INSERT INTO distributed_locks (lock_name, locked_until, locked_by) " +
                        "SELECT :lockName, :newUntil, :nodeId " +
                        "WHERE NOT EXISTS (SELECT 1 FROM distributed_locks WHERE lock_name = :lockName)"
                )
                .setParameter("lockName", lockName)
                .setParameter("newUntil", Timestamp.from(lockedUntil))
                .setParameter("nodeId", nodeId)
                .executeUpdate();

                if (inserted > 0) {
                    log.debug("[DistributedLock] Lock '{}' creat si ocupat atomic de nodul {}", lockName, nodeId);
                    return true;
                }
            }

            log.debug("[DistributedLock] Lock-ul '{}' este ocupat in prezent de alt nod", lockName);
            return false;
        } catch (Exception ex) {
            log.debug("[DistributedLock] Nu s-a putut obtine lock-ul '{}': {}", lockName, ex.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void releaseLock(String lockName) {
        try {
            if (jdbcTemplate != null) {
                jdbcTemplate.update(
                        "UPDATE distributed_locks SET locked_until = ? WHERE lock_name = ? AND locked_by = ?",
                        Timestamp.from(Instant.EPOCH),
                        lockName,
                        nodeId
                );
            } else {
                em.createQuery(
                        "UPDATE DistributedLockEntity l SET l.lockedUntil = :past " +
                        "WHERE l.lockName = :lockName AND l.lockedBy = :nodeId"
                )
                .setParameter("past", Instant.EPOCH)
                .setParameter("lockName", lockName)
                .setParameter("nodeId", nodeId)
                .executeUpdate();
            }
            log.debug("[DistributedLock] Lock '{}' eliberat de {}", lockName, nodeId);
        } catch (Exception e) {
            log.warn("[DistributedLock] Eroare la eliberarea lock-ului '{}': {}", lockName, e.getMessage());
        }
    }
}
