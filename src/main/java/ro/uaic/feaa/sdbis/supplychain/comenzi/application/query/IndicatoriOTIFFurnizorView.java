package ro.uaic.feaa.sdbis.supplychain.comenzi.application.query;

import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import java.math.BigDecimal;
import java.util.UUID;

public record IndicatoriOTIFFurnizorView(
    UUID furnizorId,
    Perioada perioada,
    int totalComenziInchise,
    int comenziOTIF,
    BigDecimal rataOTIFProcent,
    BigDecimal conformitatePretProcent,
    BigDecimal leadTimeMediuZile
) {}
