package ro.uaic.feaa.sdbis.supplychain.infrastructure.configuration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Health Indicator pentru monitorizarea starii si a lag-ului cozii Transactional Outbox.
 * Alerteaza daca evenimentele neprocesate depasesc pragul de avertizare.
 */
@Component
public class OutboxHealthIndicator implements HealthIndicator {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Health health() {
        try {
            Long unpublishedCount = em.createQuery(
                    "SELECT COUNT(o) FROM OutboxEventEntity o WHERE o.published = false", Long.class
            ).getSingleResult();

            Instant tenMinutesAgo = Instant.now().minus(10, ChronoUnit.MINUTES);
            Long staleCount = em.createQuery(
                    "SELECT COUNT(o) FROM OutboxEventEntity o WHERE o.published = false AND o.occurredOn < :cutoff", Long.class
            ).setParameter("cutoff", tenMinutesAgo).getSingleResult();

            if (staleCount > 50) {
                return Health.down()
                        .withDetail("outbox_unpublished_count", unpublishedCount)
                        .withDetail("outbox_stale_events_over_10min", staleCount)
                        .withDetail("message", "Lag critic in dispatcher-ul de Outbox!")
                        .build();
            }

            return Health.up()
                    .withDetail("outbox_unpublished_count", unpublishedCount)
                    .withDetail("outbox_stale_events_over_10min", staleCount)
                    .withDetail("status", "Outbox queue operational")
                    .build();
        } catch (Exception ex) {
            return Health.down(ex).build();
        }
    }
}

