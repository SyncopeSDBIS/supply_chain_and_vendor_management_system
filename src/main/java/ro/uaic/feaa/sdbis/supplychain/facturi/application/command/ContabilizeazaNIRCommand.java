package ro.uaic.feaa.sdbis.supplychain.facturi.application.command;

import java.util.UUID;

/**
 * Comanda pentru contabilizarea Notei de Receptie si Constatare de Diferente - NIR (I4).
 */
public record ContabilizeazaNIRCommand(UUID nirId) {}
