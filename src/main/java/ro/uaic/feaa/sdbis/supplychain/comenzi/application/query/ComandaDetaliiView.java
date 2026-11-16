package ro.uaic.feaa.sdbis.supplychain.comenzi.application.query;

import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.StareComanda;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ComandaDetaliiView(
        UUID id,
        String numarComanda,
        UUID furnizorId,
        UUID cumparatorId,
        LocalDate dataComanda,
        LocalDate dataLivrareCeruta,
        LocalDate dataLivrarePromisa,
        String depozit,
        StareComanda stareComanda,
        List<LinieDetaliiItemView> linii,
        List<LotDetaliiItemView> loturi,
        List<ExpediereDetaliiItemView> expedieri
) implements Serializable {

    public record LinieDetaliiItemView(
            String codMaterial,
            BigDecimal cantitate,
            String unitateMasura,
            BigDecimal pretUnitar,
            String moneda
    ) implements Serializable {}

    public record LotDetaliiItemView(
            UUID idLot,
            String codLot,
            String codMaterial,
            BigDecimal cantitate,
            BigDecimal cantitatePrimita,
            String unitateMasura,
            LocalDate dataProductie,
            LocalDate dataExpirare,
            String stareLot
    ) implements Serializable {}

    public record ExpediereDetaliiItemView(
            String transportator,
            String nrAwb,
            LocalDate dataExpediere,
            List<String> loturiIds
    ) implements Serializable {}
}

