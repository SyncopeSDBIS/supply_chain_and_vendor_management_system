package ro.uaic.feaa.sdbis.supplychain.comenzi.application.command;

import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.RezultatCalitateLot;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import java.util.UUID;

public record AplicaVerdictCommand(
    UUID eventId,
    IdComanda comandaId,
    IdLot lotId,
    RezultatCalitateLot rezultat
) {}
