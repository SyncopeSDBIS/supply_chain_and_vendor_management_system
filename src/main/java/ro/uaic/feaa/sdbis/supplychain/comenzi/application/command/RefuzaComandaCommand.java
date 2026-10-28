package ro.uaic.feaa.sdbis.supplychain.comenzi.application.command;

import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;

/**
 * Comanda prin care furnizorul refuza comanda de aprovizionare (UC5).
 */
public record RefuzaComandaCommand(
    IdComanda comandaId,
    String motiv
) {}
