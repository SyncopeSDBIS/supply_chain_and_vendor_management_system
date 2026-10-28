package ro.uaic.feaa.sdbis.supplychain.calitate.api.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Public Domain Event published when a vendor is blocked or sanctioned (BR-IP-08).
 */
public record FurnizorBlocatPublicEvent(
        UUID eventId,
        Instant occurredOn,
        UUID furnizorId,
        String motiv
) implements DomainEvent, Serializable {}

