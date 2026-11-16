package ro.uaic.feaa.sdbis.supplychain.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.Cantitate;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaRepo;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.LinieComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.PretUnitar;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.FacturaApplicationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.ReconciliereCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("DDD-R01: Multi-NIR Consolidated Three-Way Matching Integration Tests")
public class MultiNirReconciliereTest {

    @Autowired
    private FacturaApplicationService facturaAppService;

    @Autowired
    private FacturaRepo facturaRepo;

    @Autowired
    private NIRRepo nirRepo;

    @Autowired
    private ComandaRepo comandaRepo;

    @Test
    @DisplayName("DDD-R01 [Domain]: Reconcilierea consolidata pe 2 NIR-uri parțiale (60 + 40 = 100 buc) aproba factura integrala")
    void testDomainMultiNirReconciliationSuccess() {
        ReconciliereService service = new ReconciliereService();
        Toleranta toleranta = Toleranta.standard();
        CodMaterial material = CodMaterial.of("OTEL-INDUSTRIAL-01");

        // Comanda: 100 buc la 50 EUR
        List<LinieSumar> liniiSumar = List.of(new LinieSumar(material, BigDecimal.valueOf(100), BigDecimal.valueOf(50.0)));
        ComandaSummaryVO comanda = new ComandaSummaryVO(IdComanda.random(), liniiSumar);

        // Livrare 1: NIR-1 cu 60 bucati
        List<LinieNIR> liniiNIR1 = List.of(new LinieNIR(IdLot.random(), material, BigDecimal.valueOf(60), "BUC"));
        NIR nir1 = new NIR(UUID.randomUUID(), "NIR-PARTIAL-1", comanda.comandaId(), UUID.randomUUID(), LocalDate.now(), liniiNIR1);

        // Livrare 2: NIR-2 cu 40 bucati
        List<LinieNIR> liniiNIR2 = List.of(new LinieNIR(IdLot.random(), material, BigDecimal.valueOf(40), "BUC"));
        NIR nir2 = new NIR(UUID.randomUUID(), "NIR-PARTIAL-2", comanda.comandaId(), UUID.randomUUID(), LocalDate.now(), liniiNIR2);

        // Factura consolidata emisa de furnizor: 100 buc la 50 EUR
        List<LinieFactura> liniiFactura = List.of(new LinieFactura(
                material, BigDecimal.valueOf(100), "BUC", Suma.of(50.0, "EUR"), BigDecimal.valueOf(0.19)));
        FacturaFurnizor factura = new FacturaFurnizor(
                UUID.randomUUID(), "FC-CONS", "1001", LocalDate.now(), LocalDate.now().plusDays(30),
                comanda.comandaId(), IdFurnizor.random(), liniiFactura, Currency.getInstance("EUR")
        );

        // Executa reconcilierea consolidata
        RezultatReconciliere res = service.reconciliaza(factura, List.of(nir1, nir2), comanda, toleranta);

        assertTrue(res.aprobata(), "Reconcilierea consolidata multi-NIR trebuie sa fie aprobata cand cantitatea agregata acopera factura.");
        assertTrue(res.diferente().isEmpty(), "Nu trebuie sa existe diferente semnalate.");
    }

    @Test
    @DisplayName("DDD-R01 [Domain]: Discrepanță cantitativă detectată când factura (120 buc) depășește NIR-urile agregate (60 + 40 = 100 buc)")
    void testDomainMultiNirReconciliationDiscrepancy() {
        ReconciliereService service = new ReconciliereService();
        Toleranta toleranta = Toleranta.standard();
        CodMaterial material = CodMaterial.of("CIMENT-STRUCTURAL");

        List<LinieSumar> liniiSumar = List.of(new LinieSumar(material, BigDecimal.valueOf(120), BigDecimal.valueOf(25.0)));
        ComandaSummaryVO comanda = new ComandaSummaryVO(IdComanda.random(), liniiSumar);

        NIR nir1 = new NIR(UUID.randomUUID(), "NIR-C1", comanda.comandaId(), UUID.randomUUID(), LocalDate.now(),
                List.of(new LinieNIR(IdLot.random(), material, BigDecimal.valueOf(60), "BUC")));
        NIR nir2 = new NIR(UUID.randomUUID(), "NIR-C2", comanda.comandaId(), UUID.randomUUID(), LocalDate.now(),
                List.of(new LinieNIR(IdLot.random(), material, BigDecimal.valueOf(40), "BUC")));

        // Factura solicita 120 buc, dar s-au receptionat doar 100 buc
        List<LinieFactura> liniiFactura = List.of(new LinieFactura(
                material, BigDecimal.valueOf(120), "BUC", Suma.of(25.0, "EUR"), BigDecimal.valueOf(0.19)));
        FacturaFurnizor factura = new FacturaFurnizor(
                UUID.randomUUID(), "FC-DISC", "1002", LocalDate.now(), LocalDate.now().plusDays(30),
                comanda.comandaId(), IdFurnizor.random(), liniiFactura, Currency.getInstance("EUR")
        );

        RezultatReconciliere res = service.reconciliaza(factura, List.of(nir1, nir2), comanda, toleranta);

        assertFalse(res.aprobata(), "Reconcilierea trebuie sa respinga/blocheze factura cu discrepante cantitative.");
        assertEquals(1, res.diferente().size());
        assertTrue(res.diferente().get(0).contains("Cantitate facturata (120) difera de cantitatea din NIR (100)"));
    }

    @Test
    @DisplayName("DDD-R01 [Application]: Flux complet transactional cu salvare si tranzitie stare Factura si NIR-uri multiple")
    void testApplicationMultiNirReconciliationWorkflow() {
        IdComanda idCmd = IdComanda.random();
        IdFurnizor idFurnizor = IdFurnizor.random();
        CodMaterial mat = CodMaterial.of("TABLA-ZINCATA");

        // Salveaza comanda
        ComandaAprovizionare cmd = new ComandaAprovizionare(
                idCmd, "CMD-MULTI-NIR", idFurnizor, UUID.randomUUID(),
                LocalDate.now(), LocalDate.now().plusDays(10), "Depozit A",
                List.of(new LinieComanda(mat, Cantitate.of(100.0, "BUC"), PretUnitar.of(80.0, "RON")))
        );
        comandaRepo.add(cmd);

        // Salveaza 2 NIR-uri
        NIR nir1 = new NIR(UUID.randomUUID(), "NIR-APP-1", idCmd, UUID.randomUUID(), LocalDate.now(),
                List.of(new LinieNIR(IdLot.random(), mat, BigDecimal.valueOf(60), "BUC")));
        NIR nir2 = new NIR(UUID.randomUUID(), "NIR-APP-2", idCmd, UUID.randomUUID(), LocalDate.now(),
                List.of(new LinieNIR(IdLot.random(), mat, BigDecimal.valueOf(40), "BUC")));
        nirRepo.add(nir1);
        nirRepo.add(nir2);

        // Salveaza factura
        FacturaFurnizor factura = new FacturaFurnizor(
                UUID.randomUUID(), "FC-APP", "9001", LocalDate.now(), LocalDate.now().plusDays(30),
                idCmd, idFurnizor,
                List.of(new LinieFactura(mat, BigDecimal.valueOf(100), "BUC", Suma.of(80.0, "RON"), BigDecimal.valueOf(0.19))),
                Currency.getInstance("RON")
        );
        facturaRepo.add(factura);

        // Executa reconcilierea consolidata prin Application Service
        facturaAppService.reconcilieazaFactura(new ReconciliereCommand(
                factura.getIdFactura(), List.of(nir1.getIdNIR(), nir2.getIdNIR())));

        // Verifica starea finala a agregatelor
        FacturaFurnizor fReconciliata = facturaRepo.findById(factura.getIdFactura()).orElseThrow();
        assertEquals(StareFactura.AprobataLaPlata, fReconciliata.getStareFactura(),
                "Factura trebuie sa fie aprobata la plata dupa reconcilierea consolidata.");

        NIR n1Updated = nirRepo.findById(nir1.getIdNIR()).orElseThrow();
        NIR n2Updated = nirRepo.findById(nir2.getIdNIR()).orElseThrow();
        assertEquals(StareNIR.Reconciliat, n1Updated.getStareNIR(), "NIR 1 trebuie sa fie marcat ca Reconciliat.");
        assertEquals(StareNIR.Reconciliat, n2Updated.getStareNIR(), "NIR 2 trebuie sa fie marcat ca Reconciliat.");
    }
}
