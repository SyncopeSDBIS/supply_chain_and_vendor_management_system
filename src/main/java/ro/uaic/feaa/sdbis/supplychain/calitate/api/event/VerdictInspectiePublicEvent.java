package ro.uaic.feaa.sdbis.supplychain.calitate.api.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Public Domain Event published when an AQL inspection verdict is recorded.
 * Exposes inspection verdict across module boundaries cleanly.
 */
public record VerdictInspectiePublicEvent(
        UUID eventId,
        Instant occurredOn,
        UUID inspectieId,
        IdLot lotId,
        IdComanda comandaId,
        String verdict,
        boolean conforma
) implements DomainEvent, Serializable {}
