package ro.uaic.feaa.sdbis.supplychain.facturi.application.query;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareFactura;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record FacturaListItemView(
        UUID id,
        String serieSiNumar,
        UUID furnizorId,
        UUID comandaId,
        LocalDate dataEmitere,
        LocalDate dataScadenta,
        BigDecimal valoareTotala,
        BigDecimal sumaPlatita,
        BigDecimal restDePlata,
        String moneda,
        StareFactura stareFactura,
        boolean furnizorBlocat
) implements Serializable {}

