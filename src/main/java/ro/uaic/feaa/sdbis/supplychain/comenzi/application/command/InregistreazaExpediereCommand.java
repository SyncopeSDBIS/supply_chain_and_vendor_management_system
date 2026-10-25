package ro.uaic.feaa.sdbis.supplychain.comenzi.application.command;

import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import java.util.Set;

public record InregistreazaExpediereCommand(
    IdComanda comandaId,
    String transportator,
    String awb,
    Set<IdLot> loturi
) {}
