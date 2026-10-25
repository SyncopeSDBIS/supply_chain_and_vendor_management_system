package ro.uaic.feaa.sdbis.supplychain.shared;

import java.util.List;
import java.util.UUID;

/**
 * Port pentru salvarea si publicarea evenimentelor conform Transactional Outbox pattern.
 */
public interface OutboxRepo {
    void saveEvent(DomainEvent e);
    List<DomainEvent> pollUnpublishedEvents();
    void markAsPublished(UUID eventId);
}
