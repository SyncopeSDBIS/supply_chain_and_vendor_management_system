package ro.uaic.feaa.sdbis.supplychain.facturi.api.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ScadentaApropiataPublicEvent(
    UUID eventId,
    Instant occurredOn,
    UUID facturaId,
    LocalDate dataScadenta
) implements DomainEvent, Serializable {}
