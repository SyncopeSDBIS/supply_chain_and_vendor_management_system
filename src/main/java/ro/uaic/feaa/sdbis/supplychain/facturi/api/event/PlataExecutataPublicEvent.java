package ro.uaic.feaa.sdbis.supplychain.facturi.api.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Public Domain Event published when a bank payment execution is confirmed via Stripe.
 */
public record PlataExecutataPublicEvent(
        @NotNull UUID eventId,
        @NotNull Instant occurredOn,
        @NotNull UUID idOrdin,
        @NotNull UUID facturaId,
        @NotNull @Positive BigDecimal suma,
        @NotBlank String moneda
) implements DomainEvent, Serializable {}
