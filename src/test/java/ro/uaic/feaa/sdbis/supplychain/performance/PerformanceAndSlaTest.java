package ro.uaic.feaa.sdbis.supplychain.performance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SLA & Performance Benchmarks (Tabelul 1.2 din Project_OOAD.docx)")
public class PerformanceAndSlaTest {

    @Test
    @DisplayName("SLA Lead Time: EstimareLeadTimeService ruleaza in < 300 ms pe un istoric de 1.000 de livrari")
    void testLeadTimeEstimationPerformanceUnder300ms() {
        EstimareLeadTimeService service = new EstimareLeadTimeService(new Percentila90Strategy());

        // Generare istoric de 1.000 de livrari
        List<Integer> leadTimes = new ArrayList<>(1000);
        Random random = new Random(42);
        for (int i = 0; i < 1000; i++) {
            leadTimes.add(5 + random.nextInt(20)); // intre 5 si 25 de zile
        }
        IstoricLivrariVO istoric = new IstoricLivrariVO(leadTimes);
        MetriciLivrareVO metrici = new MetriciLivrareVO(
                IdFurnizor.of(UUID.randomUUID()),
                CodMaterial.of("OTEL-01"),
                LocalDate.now(),
                LocalDate.now().plusDays(14)
        );

        // Warm-up
        for (int i = 0; i < 50; i++) {
            service.calculeazaDataLivrare(metrici, istoric);
        }

        Instant start = Instant.now();
        EstimareLivrare estimare = service.calculeazaDataLivrare(metrici, istoric);
        long elapsedMs = Duration.between(start, Instant.now()).toMillis();

        assertNotNull(estimare);
        assertNotNull(estimare.getDataEstimata());
        assertTrue(elapsedMs < 300, "Timpul de calcul (" + elapsedMs + " ms) trebuie sa respecte SLA-ul de < 300 ms");
    }

    @Test
    @DisplayName("SLA Reconciliere: Reconcilierea in batch a 5.000 de facturi ruleaza in < 60 secunde")
    void testBatchReconciliere5000FacturiUnder60Seconds() {
        ReconciliereService reconciliereService = new ReconciliereService();
        Toleranta toleranta = Toleranta.standard();
        CodMaterial material = CodMaterial.of("ALUMINIU-02");

        List<LinieSumar> liniiSumar = List.of(new LinieSumar(material, BigDecimal.valueOf(100), BigDecimal.valueOf(50.0)));
        ComandaSummaryVO comanda = new ComandaSummaryVO(IdComanda.of(UUID.randomUUID()), liniiSumar);

        List<LinieNIR> liniiNIR = List.of(new LinieNIR(IdLot.random(), material, BigDecimal.valueOf(100), "BUC"));
        NIR nir = new NIR(UUID.randomUUID(), "NIR-BATCH", comanda.comandaId(), UUID.randomUUID(), LocalDate.now(), liniiNIR);

        List<LinieFactura> liniiFactura = List.of(new LinieFactura(material, BigDecimal.valueOf(100), "BUC", Suma.of(50.0, "EUR"), BigDecimal.valueOf(0.19)));

        FacturaFurnizor factura = new FacturaFurnizor(
                UUID.randomUUID(), "FACT", "BATCH", LocalDate.now(), LocalDate.now().plusDays(30),
                comanda.comandaId(), IdFurnizor.of(UUID.randomUUID()), liniiFactura, Currency.getInstance("EUR")
        );

        Instant start = Instant.now();
        int batchSize = 5000;
        int aprobate = 0;

        for (int i = 0; i < batchSize; i++) {
            RezultatReconciliere res = reconciliereService.reconciliaza(factura, nir, comanda, toleranta);
            if (res.aprobata()) {
                aprobate++;
            }
        }

        long elapsedSeconds = Duration.between(start, Instant.now()).toSeconds();

        assertEquals(batchSize, aprobate, "Toate cele 5.000 de facturi conforme trebuie aprobate");
        assertTrue(elapsedSeconds < 60, "Reconcilierea a 5.000 de facturi a durat " + elapsedSeconds + " s, depasind SLA-ul de 60 s");
    }
}
