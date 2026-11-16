package ro.uaic.feaa.sdbis.supplychain.comenzi.api.event;

import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Public Integration Event published when a purchase order is validated and approved.
 */
public record ComandaAprobataPublicEvent(
        @NotNull UUID eventId,
        @NotNull Instant occurredOn,
        @NotNull UUID comandaId,
        @NotNull UUID furnizorId,
        @NotNull BigDecimal valoareTotala,
        @NotNull String nivelAprobare
) implements DomainEvent, Serializable {}

