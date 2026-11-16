package ro.uaic.feaa.sdbis.supplychain.comenzi.api.event;

import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record ComandaInchisaPublicEvent(
        @NotNull UUID eventId,
        @NotNull Instant occurredOn,
        @NotNull UUID comandaId,
        @NotNull UUID furnizorId
) implements DomainEvent, Serializable {}

