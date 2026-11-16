package ro.uaic.feaa.sdbis.supplychain.calitate.application.query;

import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Calificativ;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.StareFurnizor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record FurnizorDetaliiView(
        UUID id,
        String codFiscal,
        String denumire,
        String strada,
        String numar,
        String localitate,
        String codPostal,
        Set<String> emailuri,
        String iban,
        Integer termenPlataZile,
        String moneda,
        StareFurnizor stare,
        int inspectiiNeconformeConsecutive,
        Calificativ calificativCurent,
        BigDecimal scorCurent,
        Set<String> categorii,
        Map<String, BigDecimal> preturiNegociate,
        List<IstoricCalificativItemView> istoricScorecarduri
) implements Serializable {}

