package ro.uaic.feaa.sdbis.supplychain.comenzi.application.command;

import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import java.time.LocalDate;

/**
 * Comanda pentru editarea unei comenzi in starea Creata (UC2).
 */
public record EditeazaComandaCommand(
    IdComanda comandaId,
    LocalDate dataLivrareCeruta,
    String depozit
) {}
