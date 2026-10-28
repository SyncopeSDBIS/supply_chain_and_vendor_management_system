package ro.uaic.feaa.sdbis.supplychain.facturi.application.command;

import java.util.UUID;

/**
 * Comanda pentru stornarea unui NIR (I4).
 */
public record StorneazaNIRCommand(UUID nirId, String motiv) {}
