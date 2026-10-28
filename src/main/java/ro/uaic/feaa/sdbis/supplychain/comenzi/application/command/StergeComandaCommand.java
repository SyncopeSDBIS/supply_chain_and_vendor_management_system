package ro.uaic.feaa.sdbis.supplychain.comenzi.application.command;

import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;

/**
 * Comanda pentru stergerea/anularea unei comenzi din sistem inainte de lansare (UC3).
 */
public record StergeComandaCommand(
    IdComanda comandaId,
    String motiv
) {}
