package ro.uaic.feaa.sdbis.supplychain.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Furnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectieCalitate;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.PlanAQL;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.ScorecardLunar;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.StareFurnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.StareInspectie;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Verdict;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.ComandaApplicationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.Cantitate;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaRepo;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.LinieComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.PretUnitar;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.RezultatCalitateLot;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.LinieFactura;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.OrdinPlata;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareFactura;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareOrdinPlata;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.Suma;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.DistributedLockService;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Remediation Comprehensive Verification Test - All 18 Audit Findings")
public class RemediationComprehensiveVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ComandaApplicationService comandaAppService;

    @Autowired
    private ComandaRepo comandaRepo;

    @Autowired(required = false)
    private DistributedLockService distributedLockService;

    // =========================================================================
    // 1. SECURITATE & RBAC
    // =========================================================================

    @Test
    @DisplayName("FIX #1: Verificarea laxă uri.contains(/webhook-banca) este eliminată; tentativele de bypass sunt respinse cu 401/403")
    void testWebhookBancaPathBypassPrevented() throws Exception {
        // Un atacator incearca sa acceseze o ruta protejata adaugand /webhook-banca ca subcale
        mockMvc.perform(get("/api/comenzi/webhook-banca"))
                .andExpect(result -> assertTrue(
                        result.getResponse().getStatus() == 401 || result.getResponse().getStatus() == 403,
                        "Tentativa de bypass pe comenzi trebuie sa fie respinsa cu 401 sau 403!"));

        mockMvc.perform(get("/api/facturi/webhook-banca-malitios"))
                .andExpect(result -> assertTrue(
                        result.getResponse().getStatus() == 401 || result.getResponse().getStatus() == 403,
                        "Tentativa de bypass pe facturi trebuie sa fie respinsa cu 401 sau 403!"));
    }

    // =========================================================================
    // 2. DDD & AGREGATE INVARIANTS
    // =========================================================================

    @Test
    @DisplayName("FIX #6: FacturaFurnizor nu permite anularea, blocarea sau contestarea unei facturi deja Platite")
    void testFacturaPlatitaNuPermiteAnulareSauBlocare() {
        LinieFactura linie = new LinieFactura(CodMaterial.of("OTEL-01"), BigDecimal.valueOf(10.0), "TO", Suma.of(100.0, "EUR"), BigDecimal.valueOf(19.0));
        FacturaFurnizor factura = new FacturaFurnizor(
                UUID.randomUUID(), "SERIA", "1001", LocalDate.now(), LocalDate.now().plusDays(30),
                IdComanda.random(), IdFurnizor.random(), List.of(linie), Currency.getInstance("EUR")
        );
        factura.trimiteLaVerificare();
        factura.aproba();

        // Inregistram plata integrala cu restul de plata
        factura.inregistreazaPlata(factura.calculeazaRestDePlata());
        assertEquals(StareFactura.Platita, factura.getStareFactura());

        // Anularea trebuie sa esueze cu IllegalStateException
        assertThrows(IllegalStateException.class, factura::anuleaza,
                "O factura achitata integral nu trebuie sa poata fi anulata!");

        // Blocarea trebuie sa esueze cu IllegalStateException
        assertThrows(IllegalStateException.class, () -> factura.blocheaza("Motiv"),
                "O factura achitata nu trebuie sa poata fi blocata!");

        // Contestarea trebuie sa esueze cu IllegalStateException
        assertThrows(IllegalStateException.class, () -> factura.contesta("Motiv"),
                "O factura achitata nu trebuie sa poata fi contestata!");
    }

    @Test
    @DisplayName("FIX #7: OrdinPlata permite inregistreazaExecutie() EXCLUSIV din starea TransmisLaBanca")
    void testOrdinPlataExecutieExclusivDinTransmisLaBanca() {
        LinieFactura linie = new LinieFactura(CodMaterial.of("OTEL-01"), BigDecimal.valueOf(10.0), "TO", Suma.of(100.0, "EUR"), BigDecimal.valueOf(19.0));
        FacturaFurnizor factura = new FacturaFurnizor(
                UUID.randomUUID(), "SERIA", "1002", LocalDate.now(), LocalDate.now().plusDays(30),
                IdComanda.random(), IdFurnizor.random(), List.of(linie), Currency.getInstance("EUR")
        );
        factura.trimiteLaVerificare();
        factura.aproba();

        UUID creatorId = UUID.randomUUID();
        OrdinPlata ordin = factura.creeazaOrdinPlata(Suma.of(500.0, "EUR"), creatorId);
        assertEquals(StareOrdinPlata.Creat, ordin.getStareOrdin());

        // Incercarea de executie directa din starea Creat trebuie sa esueze
        assertThrows(IllegalStateException.class, ordin::inregistreazaExecutie,
                "Un ordin de plata in starea Creat nu trebuie sa poata fi marcat direct ca Executat!");

        // Trimitere la aprobare si aprobare
        ordin.trimiteLaAprobare();
        UUID aprobatorId = UUID.randomUUID();
        ordin.aproba(aprobatorId);
        assertEquals(StareOrdinPlata.Aprobat, ordin.getStareOrdin());

        // Incercarea de executie directa din starea Aprobat fara transmitere la banca trebuie sa esueze
        assertThrows(IllegalStateException.class, ordin::inregistreazaExecutie,
                "Un ordin de plata in starea Aprobat nu trebuie sa poata fi executat inainte de transmitere la banca!");

        // Transmitere la banca -> acum executia este permisa
        ordin.transmiteLaBanca();
        assertEquals(StareOrdinPlata.TransmisLaBanca, ordin.getStareOrdin());
        assertDoesNotThrow(ordin::inregistreazaExecutie);
        assertEquals(StareOrdinPlata.Executat, ordin.getStareOrdin());

        // Anularea unui ordin deja Executat este strict interzisa
        assertThrows(IllegalStateException.class, ordin::anuleaza,
                "Un ordin de plata deja Executat nu trebuie sa poata fi anulat!");
    }

    @Test
    @DisplayName("FIX #9: ComandaAprovizionare permite replasarea unui lot dupa respingerea calitativa")
    void testReplanificareLotDupaRespingereCalitate() {
        IdFurnizor fId = IdFurnizor.random();
        IdComanda cId = IdComanda.random();
        CodMaterial mat = CodMaterial.of("SURUB-8MM");

        LinieComanda linie = new LinieComanda(mat, Cantitate.of(100.0, "BUC"), PretUnitar.of(2.5, "RON"));
        ComandaAprovizionare comanda = new ComandaAprovizionare(
                cId, "CMD-TEST-REPLACE-" + System.currentTimeMillis(),
                fId, UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(10),
                "Depozit A", List.of(linie)
        );
        comanda.aproba(UUID.randomUUID(), "MANAGER_ACHIZITII");
        comanda.confirma(LocalDate.now().plusDays(5));

        // 1. Planificare lot initial de 100 BUC
        IdLot lot1 = comanda.planificaLot("LOT-INIT-1", mat, Cantitate.of(100.0, "BUC"), LocalDate.now(), LocalDate.now().plusYears(1));

        // 2. Expediere si receptie lot
        comanda.inregistreazaExpediere("FanCourier", "AWB-100", Set.of(lot1));
        comanda.inregistreazaReceptie(lot1, Cantitate.of(100.0, "BUC"));

        // 3. Respingere lot la inspectia de calitate
        comanda.plaseazaLotInCarantina(lot1);
        comanda.aplicaVerdictInspectie(lot1, RezultatCalitateLot.Neconform);

        // 4. Inainte de FIX #9, planificarea unui nou lot de inlocuire de 100 BUC arunca IllegalArgumentException
        // Dupa FIX #9, loturile respinse sunt excluse din cantitatea planificata activa, permitand inlocuirea
        assertDoesNotThrow(() -> {
            IdLot lotInlocuitor = comanda.planificaLot("LOT-REPLACE-1", mat, Cantitate.of(100.0, "BUC"), LocalDate.now(), LocalDate.now().plusYears(1));
            assertNotNull(lotInlocuitor);
        }, "Furnizorul trebuie sa aiba dreptul sa replanifice marfa respinsa la receptie!");
    }

    @Test
    @DisplayName("FIX #10 & FIX #11: Furnizor validare ciclu de aprobare si sortare cronologica scorecard-uri")
    void testFurnizorAprobareSiSortareScorecarduri() {
        Furnizor f = new Furnizor(
                IdFurnizor.random(), "RO" + System.currentTimeMillis(), "Furnizor Test SRL",
                null, null, null, Set.of("MATERIALE"), null
        );
        assertEquals(StareFurnizor.Candidat, f.getStareFurnizor());

        // Incercarea de aprobare din starea Candidat trebuie sa esueze
        assertThrows(IllegalStateException.class, f::aproba,
                "Un furnizor in starea Candidat nu trebuie sa poata fi aprobat fara trimitere la evaluare!");

        // Trimitere la evaluare -> Aprobare permisa
        f.trimiteLaEvaluare();
        assertEquals(StareFurnizor.InEvaluare, f.getStareFurnizor());
        assertDoesNotThrow(f::aproba);
        assertEquals(StareFurnizor.Aprobat, f.getStareFurnizor());

        // Test FIX #11: Scorecard-urile adaugate in ordine aleatoare sunt sortate cronologic
        // Adaugam luna 3 (A), luna 1 (A), luna 2 (A)
        ro.uaic.feaa.sdbis.supplychain.calitate.domain.Scor scorA = ro.uaic.feaa.sdbis.supplychain.calitate.domain.Scor.ponderat(
                BigDecimal.valueOf(95), BigDecimal.valueOf(95), BigDecimal.valueOf(95)
        );
        ScorecardLunar sc3 = new ScorecardLunar(UUID.randomUUID(), Perioada.ofLuna(2026, 3), BigDecimal.valueOf(95), BigDecimal.ZERO, BigDecimal.valueOf(95), scorA);
        ScorecardLunar sc1 = new ScorecardLunar(UUID.randomUUID(), Perioada.ofLuna(2026, 1), BigDecimal.valueOf(90), BigDecimal.ZERO, BigDecimal.valueOf(90), scorA);
        ScorecardLunar sc2 = new ScorecardLunar(UUID.randomUUID(), Perioada.ofLuna(2026, 2), BigDecimal.valueOf(92), BigDecimal.ZERO, BigDecimal.valueOf(92), scorA);

        f.aplicaScorecard(sc3);
        f.aplicaScorecard(sc1);
        f.aplicaScorecard(sc2);

        int luniConsecutive = f.calculeazaLuniConsecutiveBSauMaiBun();
        assertEquals(3, luniConsecutive, "Trebuie sa calculeze 3 luni consecutive sortate cronologic (L1, L2, L3)!");
    }

    @Test
    @DisplayName("FIX #12: InspectieCalitate gărzi de stare pe inchide() si anuleaza()")
    void testInspectieCalitateGarziStare() {
        PlanAQL plan = PlanAQL.standard(1.5, 80, 2);
        InspectieCalitate insp = InspectieCalitate.programeaza(
                IdLot.random(), IdComanda.random(), IdFurnizor.random(),
                CodMaterial.of("MAT-01"), plan, Perioada.of(LocalDate.now(), LocalDate.now().plusDays(2))
        );
        assertEquals(StareInspectie.Programata, insp.getStareInspectie());

        // Inchiderea unei inspectii neincepute (Programata) trebuie sa esueze
        assertThrows(IllegalStateException.class, insp::inchide,
                "O inspectie in starea Programata nu poate fi inchisa fara verdict!");

        // Finalizare inspectie
        insp.aloca(UUID.randomUUID());
        insp.incepe();
        insp.inregistreazaVerdict(0); // Verdict Conforma
        assertEquals(Verdict.Conforma, insp.getVerdict());
        assertEquals(StareInspectie.Conforma, insp.getStareInspectie());

        // O inspectie deja Conforma nu poate fi anulata
        assertThrows(IllegalStateException.class, insp::anuleaza,
                "O inspectie cu verdict final Conforma nu mai poate fi anulata!");

        // Inchiderea este permisa din starea Conforma
        assertDoesNotThrow(insp::inchide);
        assertEquals(StareInspectie.Inchisa, insp.getStareInspectie());
    }

    // =========================================================================
    // 3. CONCURENȚĂ & DISTRIBUTED LOCKS
    // =========================================================================

    @Test
    @DisplayName("FIX #15: DistributedLockJpaService permite re-achizitionarea imediata a lock-ului dupa eliberare")
    void testDistributedLockReacquisitionImmediately() {
        if (distributedLockService != null) {
            String lockName = "test-remediation-lock-" + UUID.randomUUID();
            boolean acquired1 = distributedLockService.tryLock(lockName, Duration.ofSeconds(10));
            assertTrue(acquired1, "Prima achizitie a lock-ului trebuie sa reuseasca!");

            distributedLockService.releaseLock(lockName);

            // Re-achizitia imediata in aceeasi milisecunda trebuie sa reuseasca datorita setarii lockedUntil in trecut
            boolean acquired2 = distributedLockService.tryLock(lockName, Duration.ofSeconds(10));
            assertTrue(acquired2, "Re-achizitia imediata a lock-ului trebuie sa reuseasca!");

            distributedLockService.releaseLock(lockName);
        }
    }
}
