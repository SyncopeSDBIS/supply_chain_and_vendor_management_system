package ro.uaic.feaa.sdbis.supplychain.facturi.application.query;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareNIR;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record NIRListItemView(
        UUID id,
        String numarNIR,
        UUID comandaId,
        UUID eventSursaId,
        LocalDate dataReceptie,
        StareNIR stareNIR,
        List<LinieNIRView> linii
) implements Serializable {
    public record LinieNIRView(
            UUID lotId,
            String codMaterial,
            BigDecimal cantitateReceptionata,
            String unitateMasura
    ) implements Serializable {}
}

