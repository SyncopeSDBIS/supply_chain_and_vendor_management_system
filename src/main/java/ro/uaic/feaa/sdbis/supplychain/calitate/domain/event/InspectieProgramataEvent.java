package ro.uaic.feaa.sdbis.supplychain.calitate.domain.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import java.time.Instant;
import java.util.UUID;

public record InspectieProgramataEvent(
    UUID eventId,
    Instant occurredOn,
    UUID inspectieId,
    IdLot lotId,
    IdComanda comandaId
) implements DomainEvent {}
