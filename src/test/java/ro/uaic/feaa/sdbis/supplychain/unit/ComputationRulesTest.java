package ro.uaic.feaa.sdbis.supplychain.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Calificativ;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Scor;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.Toleranta;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Sprint 2 - Computation Rules Tests (Tabelul Gherkin 1.2.5)")
public class ComputationRulesTest {

    @Test
    @DisplayName("Scorecard weighted formula: 40% OTIF + 40% Calitate + 20% Pret -> Grade Mapping")
    void testScorecardGradeMapping() {
        // Scenariul 1: OTIF=98%, Respingere=2% (Calitate=98%), Pret=100% -> Scor = 98.4 -> A
        Scor sA = Scor.ponderat(BigDecimal.valueOf(98.0), BigDecimal.valueOf(98.0), BigDecimal.valueOf(100.0));
        assertEquals(Calificativ.A, sA.calificativ());
        assertTrue(sA.getValoare().compareTo(BigDecimal.valueOf(90.0)) >= 0);

        // Scenariul 2: OTIF=85%, Calitate=85%, Pret=90% -> Scor = 86.0 -> B
        Scor sB = Scor.ponderat(BigDecimal.valueOf(85.0), BigDecimal.valueOf(85.0), BigDecimal.valueOf(90.0));
        assertEquals(Calificativ.B, sB.calificativ());

        // Scenariul 3: OTIF=70%, Calitate=70%, Pret=75% -> Scor = 71.0 -> C
        Scor sC = Scor.ponderat(BigDecimal.valueOf(70.0), BigDecimal.valueOf(70.0), BigDecimal.valueOf(75.0));
        assertEquals(Calificativ.C, sC.calificativ());

        // Scenariul 4: OTIF=50%, Calitate=50%, Pret=60% -> Scor = 52.0 -> D
        Scor sD = Scor.ponderat(BigDecimal.valueOf(50.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(60.0));
        assertEquals(Calificativ.D, sD.calificativ());
    }

    @Test
    @DisplayName("Toleranta +-2% Three-Way Match calculation")
    void testTolerantaCalculation() {
        Toleranta t = Toleranta.standard(); // 2%

        // Exact match
        assertTrue(t.acopera(BigDecimal.valueOf(100.0), BigDecimal.valueOf(100.0)));
        // +1.5% difference -> within 2%
        assertTrue(t.acopera(BigDecimal.valueOf(100.0), BigDecimal.valueOf(101.5)));
        // -1.8% difference -> within 2%
        assertTrue(t.acopera(BigDecimal.valueOf(100.0), BigDecimal.valueOf(98.2)));

        // +3.0% difference -> exceeds 2%
        assertFalse(t.acopera(BigDecimal.valueOf(100.0), BigDecimal.valueOf(103.0)));
        // -2.5% difference -> exceeds 2%
        assertFalse(t.acopera(BigDecimal.valueOf(100.0), BigDecimal.valueOf(97.5)));
    }

    @Test
    @DisplayName("Lead time estimation strategies (MedieMobila, NetezireExponentiala, Percentila90)")
    void testLeadTimeEstimation() {
        IstoricLivrariVO istoric = new IstoricLivrariVO(Arrays.asList(10, 12, 14, 11, 13, 15, 12, 14));
        MetriciLivrareVO metrici = new MetriciLivrareVO(
            IdFurnizor.random(), CodMaterial.of("MAT-01"), LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 15)
        );

        EstimareLeadTimeService svc = new EstimareLeadTimeService(new MedieMobilaStrategy());
        EstimareLivrare e1 = svc.calculeazaDataLivrare(metrici, istoric);
        assertNotNull(e1.getDataEstimata());

        svc.setStrategie(new NetezireExponentialaStrategy(0.3));
        EstimareLivrare e2 = svc.calculeazaDataLivrare(metrici, istoric);
        assertNotNull(e2.getDataEstimata());

        svc.setStrategie(new Percentila90Strategy());
        EstimareLivrare e3 = svc.calculeazaDataLivrare(metrici, istoric);
        assertNotNull(e3.getDataEstimata());
    }
}
