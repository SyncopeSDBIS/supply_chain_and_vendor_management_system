package ro.uaic.feaa.sdbis.supplychain.comenzi.application.command;

import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

/**
 * Comanda pentru redirectionarea comenzii catre un furnizor alternativ dupa expirarea celor 48h de inactivitate (UC5x).
 */
public record RedirectioneazaComandaCommand(
    IdComanda comandaId,
    IdFurnizor nouFurnizorId,
    String motiv
) {}
