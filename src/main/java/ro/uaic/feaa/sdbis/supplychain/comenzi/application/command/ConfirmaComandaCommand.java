package ro.uaic.feaa.sdbis.supplychain.comenzi.application.command;

import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import java.time.LocalDate;

public record ConfirmaComandaCommand(IdComanda comandaId, LocalDate dataPromisa) {}
