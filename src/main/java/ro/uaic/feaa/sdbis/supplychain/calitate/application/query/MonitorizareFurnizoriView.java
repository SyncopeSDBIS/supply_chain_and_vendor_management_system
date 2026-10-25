package ro.uaic.feaa.sdbis.supplychain.calitate.application.query;

import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Calificativ;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.StareFurnizor;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record MonitorizareFurnizoriView(
    UUID idFurnizor,
    String codFiscal,
    String denumire,
    StareFurnizor stareCurenta,
    BigDecimal ultimulScorLunar,
    Calificativ ultimulCalificativ,
    int luniConsecutiveBSauMaiBun,
    BigDecimal rataRespingereCumulata,
    int numarNCRDeschise,
    int numarNCREscaladate,
    List<IstoricCalificativItemView> istoricCalificative
) {}
