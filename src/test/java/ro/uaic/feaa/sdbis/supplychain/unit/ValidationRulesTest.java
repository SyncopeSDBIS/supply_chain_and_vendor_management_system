package ro.uaic.feaa.sdbis.supplychain.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Iban;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.PlanAQL;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Verdict;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.Cantitate;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Sprint 2 - Validation Rules Tests (Tabelul 2.1, 2.2)")
public class ValidationRulesTest {

    @Test
    @DisplayName("PlanAQL invariant: numarRespingere must equal numarAcceptare + 1")
    void testPlanAQLInvariant() {
        // Valid ISO 2859-1 single sampling plan
        PlanAQL plan = PlanAQL.standard(1.5, 80, 2);
        assertEquals(2, plan.getNumarAcceptare());
        assertEquals(3, plan.getNumarRespingere());
        assertEquals(Verdict.Conforma, plan.decide(2));
        assertEquals(Verdict.Neconforma, plan.decide(3));

        // Invariant breach: Ac=2, Re=4
        assertThrows(IllegalArgumentException.class, () -> {
            new PlanAQL(BigDecimal.valueOf(1.5), 80, 2, 4);
        });
    }

    @Test
    @DisplayName("Perioada invariant: dataFinalizare must be >= dataInceput")
    void testPerioadaInvariant() {
        LocalDate start = LocalDate.of(2026, 3, 1);
        LocalDate end = LocalDate.of(2026, 3, 31);
        Perioada p = Perioada.of(start, end);
        assertTrue(p.includeData(LocalDate.of(2026, 3, 15)));

        // Invalid: end before start
        assertThrows(IllegalArgumentException.class, () -> {
            Perioada.of(end, start);
        });
    }

    @Test
    @DisplayName("IBAN validation rule")
    void testIbanValidation() {
        assertTrue(Iban.esteValid("RO49AAAA1B31007593840000"));
        assertFalse(Iban.esteValid("1234INVALID"));
        assertFalse(Iban.esteValid(null));
    }

    @Test
    @DisplayName("Cantitate validation: cannot add different units")
    void testCantitateValidation() {
        Cantitate c1 = Cantitate.of(100.0, "KG");
        Cantitate c2 = Cantitate.of(50.0, "KG");
        Cantitate sum = c1.adauga(c2);
        assertEquals(BigDecimal.valueOf(150.0), sum.getValoare());

        Cantitate c3 = Cantitate.of(10.0, "BUC");
        assertThrows(IllegalArgumentException.class, () -> c1.adauga(c3));
    }
}
