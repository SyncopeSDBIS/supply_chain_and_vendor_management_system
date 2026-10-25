package ro.uaic.feaa.sdbis.supplychain.calitate.application.query;

import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Calificativ;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import java.math.BigDecimal;

public record IstoricCalificativItemView(
    Perioada luna,
    BigDecimal scor,
    Calificativ calificativ
) {}
