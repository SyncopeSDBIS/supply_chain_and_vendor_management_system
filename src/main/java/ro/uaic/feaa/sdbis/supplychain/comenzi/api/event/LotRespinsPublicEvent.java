package ro.uaic.feaa.sdbis.supplychain.comenzi.api.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record LotRespinsPublicEvent(
        @NotNull UUID eventId,
        @NotNull Instant occurredOn,
        @NotNull UUID comandaId,
        @NotNull UUID lotId,
        @NotBlank String motiv
) implements DomainEvent, Serializable {}

