package ro.uaic.feaa.sdbis.supplychain.facturi.application.command;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.LinieNIR;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record GenereazaNIRCommand(
    String numarNIR,
    IdComanda comandaId,
    UUID eventSursaId,
    LocalDate dataReceptie,
    List<LinieNIR> linii
) {}
