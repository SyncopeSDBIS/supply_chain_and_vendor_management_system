package ro.uaic.feaa.sdbis.supplychain.comenzi.application.query;

import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.StareComanda;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record DashboardLivrariView(
    UUID comandaId,
    String numarComanda,
    UUID furnizorId,
    StareComanda stareComanda,
    LocalDate dataComanda,
    LocalDate dataLivrareCeruta,
    LocalDate dataLivrarePromisa,
    LocalDate dataLivrareEstimata,
    LocalDate dataReceptieEfectiva,
    BigDecimal totalCantitateComandata,
    BigDecimal totalCantitateReceptionata,
    BigDecimal totalCantitateRamasa,
    int zileIntarziere,
    boolean livrataLaTimpSiIntegral
) {}
