package ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record ComandaExpediataEvent(
        UUID eventId,
        Instant occurredOn,
        IdComanda comandaId,
        String transportator,
        String awb,
        Set<IdLot> loturi
) implements DomainEvent {}

