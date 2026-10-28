package ro.uaic.feaa.sdbis.supplychain.calitate.application.command;

import java.util.UUID;

public record SeteazaDisponibilitateInspectorCommand(UUID inspectorId, boolean disponibil) {}
