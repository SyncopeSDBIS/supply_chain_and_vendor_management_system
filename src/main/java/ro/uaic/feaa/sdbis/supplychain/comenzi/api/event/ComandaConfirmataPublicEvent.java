package ro.uaic.feaa.sdbis.supplychain.comenzi.api.event;

import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ComandaConfirmataPublicEvent(
        @NotNull UUID eventId,
        @NotNull Instant occurredOn,
        @NotNull UUID comandaId,
        @NotNull UUID furnizorId,
        @NotNull LocalDate dataPromisa
) implements DomainEvent, Serializable {}

