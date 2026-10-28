package ro.uaic.feaa.sdbis.supplychain.facturi.application.command;

import java.util.UUID;

/**
 * Comanda pentru contestarea unei facturi de furnizor (I3d).
 */
public record ContestaFacturaCommand(UUID facturaId, String motiv) {}
