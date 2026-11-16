package ro.uaic.feaa.sdbis.supplychain.comenzi.api.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record ComandaExpediataPublicEvent(
        @NotNull UUID eventId,
        @NotNull Instant occurredOn,
        @NotNull UUID comandaId,
        @NotBlank String transportator,
        @NotBlank String awb,
        @NotEmpty Set<UUID> loturi
) implements DomainEvent, Serializable {}

