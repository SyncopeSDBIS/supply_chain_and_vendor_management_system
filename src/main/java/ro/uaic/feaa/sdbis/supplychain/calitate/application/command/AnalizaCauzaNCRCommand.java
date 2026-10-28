package ro.uaic.feaa.sdbis.supplychain.calitate.application.command;

import java.util.UUID;

public record AnalizaCauzaNCRCommand(UUID ncrId, String cauzaRadacina) {}
