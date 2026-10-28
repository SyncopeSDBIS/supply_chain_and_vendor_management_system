package ro.uaic.feaa.sdbis.supplychain.comenzi.api.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Public Domain Event published when a purchase order is created.
 */
public record ComandaCreataPublicEvent(
        UUID eventId,
        Instant occurredOn,
        UUID comandaId,
        UUID furnizorId
) implements DomainEvent, Serializable {}

