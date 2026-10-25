package ro.uaic.feaa.sdbis.supplychain.calitate.domain.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CertificareInspectorExpiraEvent(
    UUID eventId,
    Instant occurredOn,
    UUID inspectorId,
    String denumireCertificare,
    LocalDate dataExpirare
) implements DomainEvent {}
