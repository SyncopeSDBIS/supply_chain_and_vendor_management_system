package ro.uaic.feaa.sdbis.supplychain.facturi.domain.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ScadentaApropiataEvent(
    UUID eventId,
    Instant occurredOn,
    UUID facturaId,
    LocalDate dataScadenta
) implements DomainEvent {}
