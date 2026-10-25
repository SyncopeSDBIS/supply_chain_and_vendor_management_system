package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import java.math.BigDecimal;

/**
 * Domain Service pentru calculul scorecardului lunar (Figura 20).
 * Pure Domain: fara adnotari de Spring.
 */
public class CalculScorecardService {

    public ScorecardLunar calculeazaScorecard(Perioada luna, PerformantaLivrariVO perf, BigDecimal rataRespingere) {
        return ScorecardLunar.calculeaza(luna, perf, rataRespingere);
    }
}
