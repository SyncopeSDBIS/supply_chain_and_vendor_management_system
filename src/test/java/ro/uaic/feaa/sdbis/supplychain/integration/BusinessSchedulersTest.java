package ro.uaic.feaa.sdbis.supplychain.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaRepo;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.LinieComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.Cantitate;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.PretUnitar;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaRepo;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.LinieFactura;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.Suma;
import ro.uaic.feaa.sdbis.supplychain.infrastructure.scheduler.BusinessSchedulers;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.OutboxRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@Transactional
public class BusinessSchedulersTest {

    @Autowired
    private BusinessSchedulers schedulers;

    @Autowired
    private ComandaRepo comandaRepo;

    @Autowired
    private FurnizorRepo furnizorRepo;

    @Autowired
    private InspectorRepo inspectorRepo;

    @Autowired
    private FacturaRepo facturaRepo;

    @Autowired
    private OutboxRepo outboxRepo;

    @Test
    @DisplayName("Test Schedulere: Executia tuturor celor 4 joburi programate de background")
    void testAllBusinessSchedulersExecution() {
        // 1. Setup date pentru timeout comenzi neconfirmate (> 48h)
        IdComanda idCmd = IdComanda.random();
        IdFurnizor furnizorId = IdFurnizor.random();
        ComandaAprovizionare cmdVeche = new ComandaAprovizionare(
            idCmd, "CMD-SCHED-1", furnizorId, UUID.randomUUID(),
            LocalDate.now().minusDays(5), LocalDate.now().plusDays(5), "Depozit",
            Collections.singletonList(new LinieComanda(CodMaterial.of("MAT-01"), Cantitate.of(10.0, "BUC"), PretUnitar.of(100.0, "RON")))
        );
        comandaRepo.add(cmdVeche);

        // 2. Setup inspector cu certificare care expira in < 15 zile
        InspectorCalitate inspector = new InspectorCalitate(UUID.randomUUID(), new FullName("Ion", "Popescu"));
        inspector.adaugaCertificare(new Certificare("ISO 9001 Lead Auditor", LocalDate.now().plusDays(5)));
        inspectorRepo.add(inspector);

        // 3. Setup factura aprobata la plata cu scadenta in fix 3 zile
        FacturaFurnizor factura = new FacturaFurnizor(
            UUID.randomUUID(), "FC", "999", LocalDate.now(), LocalDate.now().plusDays(3),
            idCmd, furnizorId,
            Collections.singletonList(new LinieFactura(CodMaterial.of("MAT-01"), BigDecimal.TEN, "BUC", Suma.of(100.0, "RON"), BigDecimal.valueOf(0.19))),
            Currency.getInstance("RON")
        );
        factura.trimiteLaVerificare();
        factura.aproba();
        facturaRepo.add(factura);

        // Executa schedulerele
        assertDoesNotThrow(() -> schedulers.verificaTimeoutConfirmareComenzi());
        assertDoesNotThrow(() -> schedulers.calculScorecardLunarAutomat());
        assertDoesNotThrow(() -> schedulers.verificaExpirareCertificari());
        assertDoesNotThrow(() -> schedulers.verificaScadenteFacturi());
    }
}
