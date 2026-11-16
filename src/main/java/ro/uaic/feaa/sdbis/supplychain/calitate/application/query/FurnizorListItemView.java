package ro.uaic.feaa.sdbis.supplychain.calitate.application.query;

import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Calificativ;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.StareFurnizor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record FurnizorListItemView(
        UUID id,
        String codFiscal,
        String denumire,
        String localitate,
        String iban,
        Integer termenPlataZile,
        String moneda,
        StareFurnizor stare,
        Calificativ calificativ,
        BigDecimal ultimulScor,
        Set<String> categorii
) implements Serializable {}

