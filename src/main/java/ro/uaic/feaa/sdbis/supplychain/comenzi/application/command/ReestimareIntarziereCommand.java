package ro.uaic.feaa.sdbis.supplychain.comenzi.application.command;

import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import java.time.LocalDate;

/**
 * Comanda pentru reestimarea datei de livrare in caz de intarziere notificata de furnizor (UC6y).
 */
public record ReestimareIntarziereCommand(
    IdComanda comandaId,
    LocalDate nouaDataLivrare,
    String motiv
) {}
