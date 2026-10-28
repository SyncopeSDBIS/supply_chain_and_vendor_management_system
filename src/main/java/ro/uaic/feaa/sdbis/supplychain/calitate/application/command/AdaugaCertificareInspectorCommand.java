package ro.uaic.feaa.sdbis.supplychain.calitate.application.command;

import java.time.LocalDate;
import java.util.UUID;

public record AdaugaCertificareInspectorCommand(
    UUID inspectorId,
    String denumire,
    String emitent,
    LocalDate dataObtinere,
    LocalDate dataExpirare
) {}
