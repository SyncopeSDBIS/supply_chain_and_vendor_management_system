package ro.uaic.feaa.sdbis.supplychain.facturi.application.query;

import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareOrdinPlata;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record OrdinPlataListItemView(
        UUID id,
        UUID facturaId,
        BigDecimal suma,
        String moneda,
        UUID creatDe,
        UUID aprobatDe,
        StareOrdinPlata stareOrdin,
        String referintaBancara,
        String motivRespingere,
        java.time.Instant dataAprobare,
        java.time.Instant dataExecutie
) implements Serializable {}

