package ro.uaic.feaa.sdbis.supplychain.facturi.application.command;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.LinieFactura;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

public record InregistreazaFacturaCommand(
    String serie,
    String numar,
    LocalDate dataEmitere,
    LocalDate dataScadenta,
    IdComanda comandaId,
    IdFurnizor furnizorId,
    List<LinieFactura> linii,
    Currency moneda
) {}
