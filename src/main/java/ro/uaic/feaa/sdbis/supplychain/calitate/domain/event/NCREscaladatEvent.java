package ro.uaic.feaa.sdbis.supplychain.calitate.domain.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import java.time.Instant;
import java.util.UUID;

public record NCREscaladatEvent(
    UUID eventId,
    Instant occurredOn,
    UUID ncrId,
    IdFurnizor furnizorId
) implements DomainEvent {}
