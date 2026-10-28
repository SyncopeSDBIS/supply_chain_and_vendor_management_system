package ro.uaic.feaa.sdbis.supplychain.calitate.api.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record FurnizorSuspendatPublicEvent(
    UUID eventId,
    Instant occurredOn,
    IdFurnizor furnizorId,
    String motiv
) implements DomainEvent, Serializable {}
