package ro.uaic.feaa.sdbis.supplychain.calitate.domain.computation;

import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Furnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectieCalitate;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.PerformantaLivrariVO;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.ScorecardLunar;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainComputationService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Contract pentru Serviciul de Calcul Calitate si Scorecard Furnizori (Sprint 2 - ADR-003).
 */
@DomainComputationService
public interface ICalculScorecardService {

    /**
     * Entity Field Computation: Calculeaza scorecardul lunar al furnizorului.
     */
    ScorecardLunar calculeazaScorecard(Perioada luna, PerformantaLivrariVO perf, BigDecimal rataRespingere);

    /**
     * Entity View Computation: Agrega indicatorii globali de calitate intr-un dashboard.
     */
    CalitateDashboardView calculeazaDashboard(List<Furnizor> furnizori, List<InspectieCalitate> inspectii);
}
