package ro.uaic.feaa.sdbis.supplychain.comenzi.api.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record EstimareDepasesteCerereaPublicEvent(
    UUID eventId,
    Instant occurredOn,
    IdComanda comandaId,
    LocalDate dataCeruta,
    LocalDate dataEstimata
) implements DomainEvent, Serializable {}
