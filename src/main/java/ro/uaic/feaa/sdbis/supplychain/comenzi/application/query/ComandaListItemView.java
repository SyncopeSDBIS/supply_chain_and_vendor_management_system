package ro.uaic.feaa.sdbis.supplychain.comenzi.application.query;

import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.StareComanda;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ComandaListItemView(
        UUID id,
        String numarComanda,
        UUID furnizorId,
        UUID cumparatorId,
        LocalDate dataComanda,
        LocalDate dataLivrareCeruta,
        LocalDate dataLivrarePromisa,
        String depozit,
        StareComanda stareComanda,
        int numarLinii,
        int numarLoturi,
        BigDecimal valoareTotalaEstimata
) implements Serializable {}

