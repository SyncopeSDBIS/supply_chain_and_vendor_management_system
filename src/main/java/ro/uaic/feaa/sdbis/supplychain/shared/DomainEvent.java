package ro.uaic.feaa.sdbis.supplychain.shared;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Contract comun pentru toate evenimentele de domeniu din sistem.
 */
public interface DomainEvent extends Serializable {
    UUID eventId();
    Instant occurredOn();
}
