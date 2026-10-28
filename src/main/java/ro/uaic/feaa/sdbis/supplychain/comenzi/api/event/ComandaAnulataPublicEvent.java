package ro.uaic.feaa.sdbis.supplychain.comenzi.api.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Public Domain Event published when an order is cancelled in cascade.
 */
public record ComandaAnulataPublicEvent(
        UUID eventId,
        Instant occurredOn,
        UUID comandaId,
        String motiv
) implements DomainEvent, Serializable {}

