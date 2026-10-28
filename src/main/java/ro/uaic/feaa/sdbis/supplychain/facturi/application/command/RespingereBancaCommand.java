package ro.uaic.feaa.sdbis.supplychain.facturi.application.command;

import java.util.UUID;

/**
 * Comanda pentru inregistrarea respingerii platii de catre banca (I7a).
 */
public record RespingereBancaCommand(UUID ordinId, String motiv) {}
