package ro.uaic.feaa.sdbis.supplychain.shared;

import java.time.Instant;
import java.util.UUID;

/**
 * Reprezentare generica sau fallback pentru evenimente de domeniu persistate in Outbox/Inbox.
 */
public record StoredDomainEvent(
    UUID eventId,
    Instant occurredOn,
    String eventType,
    String payload
) implements DomainEvent {}
