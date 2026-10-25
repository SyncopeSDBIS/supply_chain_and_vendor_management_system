package ro.uaic.feaa.sdbis.supplychain.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.*;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.computation.CalitateDashboardView;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.computation.ICalculScorecardService;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.factory.FurnizorFactory;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.factory.InspectieCalitateFactory;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.computation.ComandaComputationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.computation.ComandaSummaryView;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.computation.IComandaComputationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.factory.ComandaFactory;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.computation.IReconciliereComputationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.computation.PlatiSummaryView;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.computation.ReconciliereComputationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.factory.FacturaFactory;
import ro.uaic.feaa.sdbis.supplychain.shared.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Sprint 2 - Computation Services Tests (ADR-003)")
public class ComputationServicesTest {

    private IComandaComputationService comandaComputationService;
    private ICalculScorecardService calculScorecardService;
    private IReconciliereComputationService reconciliereComputationService;

    @BeforeEach
    void setUp() {
        comandaComputationService = new ComandaComputationService();
        calculScorecardService = new CalculScorecardService();
        reconciliereComputationService = new ReconciliereComputationService();
    }

    // =========================================================================
    // 1. ComandaComputationService Tests
    // =========================================================================

    @Test
    @DisplayName("ComandaComputation: Calcul subtotal, TVA 19%, total cu TVA si sumar view")
    void testComandaTotalsAndSummaryView() {
        LinieComanda l1 = new LinieComanda(
            CodMaterial.of("MAT-01"),
            Cantitate.of(100.0, "BUC"),
            PretUnitar.of(20.0, "RON")
        );
        LinieComanda l2 = new LinieComanda(
            CodMaterial.of("MAT-02"),
            Cantitate.of(50.0, "BUC"),
            PretUnitar.of(40.0, "RON")
        );

        ComandaAprovizionare comanda = ComandaFactory.creeaza(
            "CMD-CALC-01",
            IdFurnizor.random(),
            UUID.randomUUID(),
            LocalDate.now(),
            LocalDate.now().plusDays(15),
            "DEP-1",
            List.of(l1, l2)
        );

        BigDecimal subtotal = comandaComputationService.calculSubtotal(comanda);
        BigDecimal tva = comandaComputationService.calculTVA(comanda, new BigDecimal("0.19"));
        BigDecimal total = comandaComputationService.calculTotalCuTVA(comanda, new BigDecimal("0.19"));

        assertEquals(new BigDecimal("4000.00"), subtotal);
        assertEquals(new BigDecimal("760.00"), tva);
        assertEquals(new BigDecimal("4760.00"), total);

        ComandaSummaryView view = comandaComputationService.genereazaSumar(comanda, new BigDecimal("0.19"));
        assertNotNull(view);
        assertEquals("CMD-CALC-01", view.numarComanda());
        assertEquals(new BigDecimal("4000.00"), view.subtotal());
        assertEquals(new BigDecimal("760.00"), view.tva());
        assertEquals(new BigDecimal("4760.00"), view.totalCuTVA());
        assertEquals(2, view.numarLinii());
        assertEquals(0.0, view.procentLivrare());
        assertFalse(view.esteLivrataComplet());
    }

    @Test
    @DisplayName("ComandaComputation: Calcul procent livrare la receptie partiala si totala")
    void testComandaProcentLivrareCalculation() {
        LinieComanda l = new LinieComanda(
            CodMaterial.of("MAT-01"),
            Cantitate.of(100.0, "KG"),
            PretUnitar.of(10.0, "RON")
        );
        ComandaAprovizionare comanda = ComandaFactory.creeaza(
            "CMD-CALC-02",
            IdFurnizor.random(),
            UUID.randomUUID(),
            LocalDate.now(),
            LocalDate.now().plusDays(10),
            "DEP-1",
            List.of(l)
        );

        assertEquals(0.0, comandaComputationService.calculProcentLivrare(comanda));

        comanda.confirma(LocalDate.now().plusDays(5));
        IdLot lot = comanda.planificaLot("LOT-01", CodMaterial.of("MAT-01"), Cantitate.of(100.0, "KG"), LocalDate.now(), LocalDate.now().plusYears(1));
        comanda.inregistreazaExpediere("TR", "AWB", Set.of(lot));

        comanda.inregistreazaReceptie(lot, Cantitate.of(40.0, "KG"));
        assertEquals(40.0, comandaComputationService.calculProcentLivrare(comanda));

        comanda.inregistreazaReceptie(lot, Cantitate.of(60.0, "KG"));
        assertEquals(100.0, comandaComputationService.calculProcentLivrare(comanda));
        assertTrue(comanda.esteAcoperitaIntegral());
    }

    // =========================================================================
    // 2. CalculScorecardService Tests (Field & View Computations)
    // =========================================================================

    @Test
    @DisplayName("CalculScorecard: Formule scorecard lunar si calificare furnizori")
    void testScorecardCalculation() {
        Perioada luna = Perioada.ofLuna(2026, 3);
        IdFurnizor fId = IdFurnizor.random();
        PerformantaLivrariVO perf = new PerformantaLivrariVO(fId, luna, BigDecimal.valueOf(95.0), BigDecimal.valueOf(98.0));
        BigDecimal rataRespingere = BigDecimal.valueOf(1.5);

        ScorecardLunar sc = calculScorecardService.calculeazaScorecard(luna, perf, rataRespingere);
        assertNotNull(sc);
        assertNotNull(sc.calificativ());
        assertTrue(sc.getScor().getValoare().compareTo(BigDecimal.valueOf(90)) >= 0);
    }

    @Test
    @DisplayName("CalculScorecard: Entity View Computation - Dashboard Calitate")
    void testCalitateDashboardViewComputation() {
        Furnizor f1 = FurnizorFactory.creeaza(
            "RO111", "F1 SRL", new Adresa("S1", "Iasi", "700000", "RO"),
            Set.of(new Email("f1@test.ro")), new Iban("RO49AAAA1B31007593840000"),
            Set.of("CAT1"), new ConditiiContractuale(30, Currency.getInstance("RON"), Collections.emptyMap())
        );
        f1.trimiteLaEvaluare();
        f1.aproba();

        Furnizor f2 = FurnizorFactory.creeaza(
            "RO222", "F2 SRL", new Adresa("S2", "Iasi", "700000", "RO"),
            Set.of(new Email("f2@test.ro")), new Iban("RO49AAAA1B31007593840000"),
            Set.of("CAT2"), new ConditiiContractuale(30, Currency.getInstance("RON"), Collections.emptyMap())
        );
        f2.blocheaza("Incalcari grave repetate");

        PlanAQL plan = PlanAQL.standard(1.0, 50, 1);
        InspectieCalitate ins1 = InspectieCalitateFactory.programeaza(
            IdLot.random(), IdComanda.random(), f1.getIdFurnizor(), CodMaterial.of("M1"), plan, Perioada.lunaCurenta()
        );
        ins1.aloca(UUID.randomUUID());
        ins1.incepe();
        ins1.inregistreazaVerdict(0); // Verdict Conforma

        InspectieCalitate ins2 = InspectieCalitateFactory.programeaza(
            IdLot.random(), IdComanda.random(), f2.getIdFurnizor(), CodMaterial.of("M2"), plan, Perioada.lunaCurenta()
        );
        ins2.aloca(UUID.randomUUID());
        ins2.incepe();
        ins2.inregistreazaVerdict(5); // Verdict Neconforma

        CalitateDashboardView dashboard = calculScorecardService.calculeazaDashboard(
            List.of(f1, f2), List.of(ins1, ins2)
        );

        assertNotNull(dashboard);
        assertEquals(2, dashboard.totalFurnizori());
        assertEquals(1, dashboard.furnizoriAprobati());
        assertEquals(1, dashboard.furnizoriBlocati());
        assertEquals(2, dashboard.totalInspectii());
        assertEquals(1, dashboard.inspectiiAdmise());
        assertEquals(1, dashboard.inspectiiRespinse());
        assertEquals(50.0, dashboard.rataConformitateLoturi());
    }

    // =========================================================================
    // 3. ReconciliereComputationService Tests (Field & View Computations)
    // =========================================================================

    @Test
    @DisplayName("ReconciliereComputation: Rest total de plata, indicatori plati si procent achitare")
    void testFacturaFinancialComputations() {
        IdComanda cId = IdComanda.random();
        IdFurnizor fId = IdFurnizor.random();

        LinieFactura lf1 = new LinieFactura(
            CodMaterial.of("M1"), BigDecimal.valueOf(10), "BUC",
            new Suma(BigDecimal.valueOf(100), Currency.getInstance("RON")), BigDecimal.valueOf(0.19)
        );
        FacturaFurnizor f1 = FacturaFactory.creeaza("FC", "1", LocalDate.now(), LocalDate.now().plusDays(30), cId, fId, List.of(lf1), Currency.getInstance("RON"));
        f1.trimiteLaVerificare();
        f1.aproba();

        f1.inregistreazaPlata(new Suma(BigDecimal.valueOf(500), Currency.getInstance("RON")));

        LinieFactura lf2 = new LinieFactura(
            CodMaterial.of("M2"), BigDecimal.valueOf(20), "BUC",
            new Suma(BigDecimal.valueOf(100), Currency.getInstance("RON")), BigDecimal.valueOf(0.19)
        );
        FacturaFurnizor f2 = FacturaFactory.creeaza("FC", "2", LocalDate.now(), LocalDate.now().plusDays(30), cId, fId, List.of(lf2), Currency.getInstance("RON"));

        double procentF1 = reconciliereComputationService.calculeazaProcentAchitare(f1);
        assertEquals(42.02, procentF1);

        BigDecimal restTotal = reconciliereComputationService.calculeazaRestPlataTotal(List.of(f1, f2));
        assertEquals(new BigDecimal("3070.00"), restTotal);

        PlatiSummaryView indicatori = reconciliereComputationService.calculeazaIndicatoriPlati(List.of(f1, f2));
        assertNotNull(indicatori);
        assertEquals(new BigDecimal("3570.00"), indicatori.totalFacturat());
        assertEquals(new BigDecimal("500.00"), indicatori.totalAchitat());
        assertEquals(new BigDecimal("3070.00"), indicatori.totalRestant());
        assertEquals(2, indicatori.numarFacturi());
        assertEquals(0, indicatori.facturiAchitateIntegral());
        assertEquals(2, indicatori.facturiRestante());
    }
}
