package ro.uaic.feaa.sdbis.supplychain.facturi.domain.event;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.Suma;
import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record PlataExecutataEvent(
    UUID eventId,
    Instant occurredOn,
    UUID ordinId,
    UUID facturaId,
    Suma suma
) implements DomainEvent {}
