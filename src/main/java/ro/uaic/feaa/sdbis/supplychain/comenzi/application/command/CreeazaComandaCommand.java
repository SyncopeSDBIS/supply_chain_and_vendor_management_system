package ro.uaic.feaa.sdbis.supplychain.comenzi.application.command;

import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.LinieComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreeazaComandaCommand(
    String numar,
    IdFurnizor furnizorId,
    UUID cumparatorId,
    LocalDate dataComanda,
    LocalDate dataLivrareCeruta,
    String depozit,
    List<LinieComanda> linii
) {}
