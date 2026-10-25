package ro.uaic.feaa.sdbis.supplychain.facturi.application.query;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareFactura;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ScadentarFacturiView(
    UUID idFactura,
    String serieNumar,
    UUID furnizorId,
    UUID comandaId,
    LocalDate dataEmitere,
    LocalDate dataScadenta,
    long zilePanaLaScadenta,
    BigDecimal valoareTotala,
    BigDecimal sumaPlatita,
    BigDecimal restDePlata,
    StareFactura stareFactura,
    boolean furnizorBlocat,
    boolean alertaScadentaActiva
) {}
