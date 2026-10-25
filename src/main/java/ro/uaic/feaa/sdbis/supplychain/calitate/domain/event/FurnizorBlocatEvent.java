package ro.uaic.feaa.sdbis.supplychain.calitate.domain.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import java.time.Instant;
import java.util.UUID;

public record FurnizorBlocatEvent(
    UUID eventId,
    Instant occurredOn,
    IdFurnizor furnizorId,
    String motiv
) implements DomainEvent {}
