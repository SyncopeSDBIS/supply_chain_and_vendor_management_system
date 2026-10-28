package ro.uaic.feaa.sdbis.supplychain.calitate.api.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record NCREscaladatPublicEvent(
    UUID eventId,
    Instant occurredOn,
    UUID ncrId,
    IdFurnizor furnizorId,
    int nivelEscaladare
) implements DomainEvent, Serializable {}
