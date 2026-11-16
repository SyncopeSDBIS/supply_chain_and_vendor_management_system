package ro.uaic.feaa.sdbis.supplychain.facturi.application.query;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareFactura;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record FacturaDetaliiView(
        UUID id,
        String serie,
        String numar,
        UUID furnizorId,
        UUID comandaId,
        LocalDate dataEmitere,
        LocalDate dataScadenta,
        BigDecimal valoareFaraTva,
        BigDecimal valoareTva,
        BigDecimal valoareTotala,
        BigDecimal sumaPlatita,
        BigDecimal restDePlata,
        String moneda,
        StareFactura stareFactura,
        boolean furnizorBlocat,
        List<LinieFacturaDetaliiItemView> linii
) implements Serializable {
    public record LinieFacturaDetaliiItemView(
            String codMaterial,
            BigDecimal cantitate,
            String unitateMasura,
            BigDecimal pretUnitar,
            String moneda,
            BigDecimal cotaTva
    ) implements Serializable {}
}

