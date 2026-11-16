package ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;

import java.time.Instant;
import java.util.UUID;

public record ComandaInchisaEvent(
        UUID eventId,
        Instant occurredOn,
        IdComanda comandaId
) implements DomainEvent {}

