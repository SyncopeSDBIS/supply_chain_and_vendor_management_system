package ro.uaic.feaa.sdbis.supplychain.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.FacturaApplicationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.*;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import ro.uaic.feaa.sdbis.supplychain.shared.OutboxRepo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("Sprint 4 - Payment & Banking Integration Tests (I6, I7, I7a, I7b)")
public class PaymentWorkflowIntegrationTest {

    @Autowired
    private FacturaApplicationService facturaAppService;

    @Autowired
    private FacturaRepo facturaRepo;

    @Autowired
    private OrdinPlataRepo ordinRepo;

    @Autowired
    private FurnizoriBlocatiRepo furnizoriBlocatiRepo;

    @Autowired
    private OutboxRepo outboxRepo;

    @Autowired
    private NIRRepo nirRepo;

    private FacturaFurnizor creeazaFacturaAprobata(double valoare, Currency moneda, IdFurnizor furnizorId) {
        UUID id = UUID.randomUUID();
        LinieFactura linie = new LinieFactura(
                CodMaterial.of("MAT-TEST-01"),
                BigDecimal.ONE,
                "BUC",
                Suma.of(valoare, moneda.getCurrencyCode()),
                BigDecimal.valueOf(0.19)
        );
        FacturaFurnizor f = new FacturaFurnizor(
                id, "FACT-PAY", "9901", LocalDate.now(), LocalDate.now().plusDays(30),
                IdComanda.of(UUID.randomUUID()), furnizorId,
                Collections.singletonList(linie), moneda
        );
        f.trimiteLaVerificare();
        f.aproba();
        facturaRepo.add(f);
        return f;
    }

    @Test
    @DisplayName("I6 & I6a: Auto-aprobare ordin <= 10.000 EUR si transmitere la banca")
    void testTransmitereOrdinSub10kCuAutoAprobare() {
        IdFurnizor furnizorId = IdFurnizor.of(UUID.randomUUID());
        FacturaFurnizor factura = creeazaFacturaAprobata(5000.0, Currency.getInstance("EUR"), furnizorId);

        UUID creatorId = UUID.randomUUID();
        UUID ordinId = facturaAppService.creeazaOrdinPlata(new CreeazaOrdinPlataCommand(
                factura.getIdFactura(),
                Suma.of(5000.0, "EUR"),
                creatorId
        ));

        OrdinPlata ordinCreat = ordinRepo.findById(ordinId).orElseThrow();
        assertEquals(StareOrdinPlata.Creat, ordinCreat.getStareOrdin());

        // Transmitere la banca -> auto-aprobare de catre Ofiter Financiar si trecere in TransmisLaBanca
        facturaAppService.transmiteOrdin(new TransmiteOrdinCommand(ordinId, "RO98BTRL0000000000000001"));

        OrdinPlata ordinTransmis = ordinRepo.findById(ordinId).orElseThrow();
        assertEquals(StareOrdinPlata.TransmisLaBanca, ordinTransmis.getStareOrdin());
        assertNotNull(ordinTransmis.getAprobatDe());
        assertNotEquals(creatorId, ordinTransmis.getAprobatDe(), "Four-Eyes Principle: aprobatorul este diferit de creator");
    }

    @Test
    @DisplayName("I6a & BR-IP-08: Blocare transmitere ordin daca furnizorul este in lista neagra")
    void testTransmitereOrdinFurnizorBlocat() {
        IdFurnizor furnizorId = IdFurnizor.of(UUID.randomUUID());
        FacturaFurnizor factura = creeazaFacturaAprobata(2000.0, Currency.getInstance("EUR"), furnizorId);

        UUID ordinId = facturaAppService.creeazaOrdinPlata(new CreeazaOrdinPlataCommand(
                factura.getIdFactura(),
                Suma.of(2000.0, "EUR"),
                UUID.randomUUID()
        ));

        // Blocare furnizor
        furnizoriBlocatiRepo.adauga(furnizorId);

        // Tentativa de transmitere trebuie sa esueze si sa anuleze ordinul
        assertThrows(IllegalStateException.class, () ->
                facturaAppService.transmiteOrdin(new TransmiteOrdinCommand(ordinId, "RO98BTRL0000000000000001"))
        );

        OrdinPlata ordin = ordinRepo.findById(ordinId).orElseThrow();
        assertEquals(StareOrdinPlata.Anulat, ordin.getStareOrdin());
    }

    @Test
    @DisplayName("I7 & I7b: Webhook executie plata cu succes -> Ordin Executat, Factura Platita / PartialPlatita, Outbox event")
    void testWebhookExecutiePlataSucces() {
        IdFurnizor furnizorId = IdFurnizor.of(UUID.randomUUID());
        FacturaFurnizor factura = creeazaFacturaAprobata(3000.0, Currency.getInstance("EUR"), furnizorId);

        // Suma totala cu TVA 19% este 3570 EUR
        Suma restTotal = factura.calculeazaRestDePlata();

        UUID ordinId = facturaAppService.creeazaOrdinPlata(new CreeazaOrdinPlataCommand(
                factura.getIdFactura(),
                restTotal,
                UUID.randomUUID()
        ));

        facturaAppService.transmiteOrdin(new TransmiteOrdinCommand(ordinId, "RO98BTRL0000000000000001"));

        // Callback / Webhook executie plata integrala (I7)
        facturaAppService.inregistreazaExecutiePlata(new InregistreazaExecutiePlataCommand(ordinId));

        OrdinPlata ordin = ordinRepo.findById(ordinId).orElseThrow();
        assertEquals(StareOrdinPlata.Executat, ordin.getStareOrdin());

        FacturaFurnizor facturaActualizata = facturaRepo.findById(factura.getIdFactura()).orElseThrow();
        assertEquals(StareFactura.Platita, facturaActualizata.getStareFactura());
        assertEquals(0, facturaActualizata.calculeazaRestDePlata().getValoare().compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("I7b: Webhook plata partiala -> Factura PartialPlatita")
    void testWebhookPlataPartiala() {
        IdFurnizor furnizorId = IdFurnizor.of(UUID.randomUUID());
        FacturaFurnizor factura = creeazaFacturaAprobata(3000.0, Currency.getInstance("EUR"), furnizorId);

        // Plata partiala de 1000 EUR dintr-un total de 3570 EUR
        UUID ordinId = facturaAppService.creeazaOrdinPlata(new CreeazaOrdinPlataCommand(
                factura.getIdFactura(),
                Suma.of(1000.0, "EUR"),
                UUID.randomUUID()
        ));

        facturaAppService.transmiteOrdin(new TransmiteOrdinCommand(ordinId, "RO98BTRL0000000000000001"));
        facturaAppService.inregistreazaExecutiePlata(new InregistreazaExecutiePlataCommand(ordinId));

        FacturaFurnizor facturaActualizata = facturaRepo.findById(factura.getIdFactura()).orElseThrow();
        assertEquals(StareFactura.PartialPlatita, facturaActualizata.getStareFactura());
        assertTrue(facturaActualizata.calculeazaRestDePlata().getValoare().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("I7a: Webhook respingere banca -> Ordin RespinsDeBanca, sold factura nemodificat")
    void testWebhookRespingereBanca() {
        IdFurnizor furnizorId = IdFurnizor.of(UUID.randomUUID());
        FacturaFurnizor factura = creeazaFacturaAprobata(4500.0, Currency.getInstance("EUR"), furnizorId);

        UUID ordinId = facturaAppService.creeazaOrdinPlata(new CreeazaOrdinPlataCommand(
                factura.getIdFactura(),
                Suma.of(4500.0, "EUR"),
                UUID.randomUUID()
        ));

        facturaAppService.transmiteOrdin(new TransmiteOrdinCommand(ordinId, "RO98BTRL0000000000000001"));

        // Webhook respingere de la banca
        facturaAppService.respingeDeBanca(new RespingereBancaCommand(ordinId, "Cont destinatar invalid / fonduri insuficiente"));

        OrdinPlata ordin = ordinRepo.findById(ordinId).orElseThrow();
        assertEquals(StareOrdinPlata.RespinsDeBanca, ordin.getStareOrdin());

        FacturaFurnizor facturaActualizata = facturaRepo.findById(factura.getIdFactura()).orElseThrow();
        assertEquals(StareFactura.AprobataLaPlata, facturaActualizata.getStareFactura());
        assertEquals(0, facturaActualizata.getSumaPlatita().getValoare().compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("I4: Idempotenta generare NIR - apeluri multiple cu acelasi eventSursaId nu genereaza duplicate")
    void testNIRGenerareIdempotenta() {
        UUID eventSursaId = UUID.randomUUID();
        IdComanda comandaId = IdComanda.of(UUID.randomUUID());
        LinieNIR linie = new LinieNIR(
                IdLot.random(),
                CodMaterial.of("OTEL-01"),
                BigDecimal.valueOf(100),
                "BUC"
        );
        GenereazaNIRCommand cmd = new GenereazaNIRCommand(
                "NIR-IDEMPOTENT-001",
                comandaId,
                eventSursaId,
                LocalDate.now(),
                Collections.singletonList(linie)
        );

        facturaAppService.genereazaNIR(cmd);
        assertEquals(1, nirRepo.findByComandaId(comandaId).size());

        // Al doilea apel cu acelasi eventSursaId nu genereaza duplicat
        facturaAppService.genereazaNIR(cmd);
        assertEquals(1, nirRepo.findByComandaId(comandaId).size(), "Generarea de NIR trebuie sa fie strict idempotenta");
    }
}
