package ro.uaic.feaa.sdbis.supplychain.facturi.application.command;

import java.util.UUID;

/**
 * Comanda pentru aprobarea unui ordin de plata de catre Second Approver (I5b)
 * sau de catre Ofiterul Financiar.
 */
public record AprobaOrdinCommand(UUID ordinId, UUID aprobatorId) {}
