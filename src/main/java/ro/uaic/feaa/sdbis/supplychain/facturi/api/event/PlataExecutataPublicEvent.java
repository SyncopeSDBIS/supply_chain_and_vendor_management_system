package ro.uaic.feaa.sdbis.supplychain.facturi.api.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Public Domain Event published when a bank payment execution is confirmed via Stripe.
 */
public record PlataExecutataPublicEvent(
        UUID eventId,
        Instant occurredOn,
        UUID idOrdin,
        UUID facturaId,
        BigDecimal suma,
        String moneda
) implements DomainEvent, Serializable {}

