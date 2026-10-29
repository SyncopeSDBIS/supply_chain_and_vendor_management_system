package ro.uaic.feaa.sdbis.supplychain.calitate.api.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
        @NotNull UUID eventId,
        @NotNull Instant occurredOn,
        @NotNull UUID inspectieId,
        @NotNull IdLot lotId,
        @NotNull IdComanda comandaId,
        @NotBlank String verdict,
        boolean conforma
) implements DomainEvent, Serializable {}
