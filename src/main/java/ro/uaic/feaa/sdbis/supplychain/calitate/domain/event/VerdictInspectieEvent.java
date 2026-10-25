package ro.uaic.feaa.sdbis.supplychain.calitate.domain.event;

import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Verdict;
import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import java.time.Instant;
import java.util.UUID;

public record VerdictInspectieEvent(
    UUID eventId,
    Instant occurredOn,
    IdLot lotId,
    IdComanda comandaId,
    Verdict verdict
) implements DomainEvent {}
