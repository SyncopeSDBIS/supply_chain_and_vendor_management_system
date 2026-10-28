package ro.uaic.feaa.sdbis.supplychain.calitate.api.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CertificareInspectorExpiraPublicEvent(
    UUID eventId,
    Instant occurredOn,
    UUID inspectorId,
    String denumireCertificare,
    LocalDate dataExpirare
) implements DomainEvent, Serializable {}
