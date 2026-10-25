package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.calitate.domain.computation.CalitateDashboardView;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.computation.ICalculScorecardService;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Domain Service pentru calculul scorecardului lunar si indicatorilor de calitate (Figura 20).
 * Pure Domain: fara adnotari de Spring (ADR-011).
 */
public class CalculScorecardService implements ICalculScorecardService {

    @Override
    public ScorecardLunar calculeazaScorecard(Perioada luna, PerformantaLivrariVO perf, BigDecimal rataRespingere) {
        return ScorecardLunar.calculeaza(luna, perf, rataRespingere);
    }

    @Override
    public CalitateDashboardView calculeazaDashboard(List<Furnizor> furnizori, List<InspectieCalitate> inspectii) {
        int totalFurnizori = furnizori != null ? furnizori.size() : 0;
        int furnizoriAprobati = 0;
        int furnizoriBlocati = 0;

        if (furnizori != null) {
            for (Furnizor f : furnizori) {
                if (f.getStareFurnizor() == StareFurnizor.Aprobat) furnizoriAprobati++;
                if (f.getStareFurnizor() == StareFurnizor.Blocat) furnizoriBlocati++;
            }
        }

        int totalInspectii = inspectii != null ? inspectii.size() : 0;
        int inspectiiAdmise = 0;
        int inspectiiRespinse = 0;

        if (inspectii != null) {
            for (InspectieCalitate insp : inspectii) {
                if (insp.getVerdict() == Verdict.Conforma) inspectiiAdmise++;
                if (insp.getVerdict() == Verdict.Neconforma) inspectiiRespinse++;
            }
        }

        double rataConformitate = 100.0;
        if (totalInspectii > 0) {
            rataConformitate = BigDecimal.valueOf(inspectiiAdmise)
                .divide(BigDecimal.valueOf(totalInspectii), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
        }

        return new CalitateDashboardView(
            totalFurnizori,
            furnizoriAprobati,
            furnizoriBlocati,
            totalInspectii,
            inspectiiAdmise,
            inspectiiRespinse,
            rataConformitate
        );
    }
}
