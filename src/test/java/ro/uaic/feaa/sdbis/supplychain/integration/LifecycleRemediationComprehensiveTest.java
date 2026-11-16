package ro.uaic.feaa.sdbis.supplychain.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.ComandaApplicationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.policy.PoliticaAprobareComanda.NivelAprobare;
import ro.uaic.feaa.sdbis.supplychain.notificari.NotificariDispatcherService;
import ro.uaic.feaa.sdbis.supplychain.shared.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@org.springframework.test.context.TestPropertySource(properties = {
    "security.allow-anonymous-fallback=true",
    "security.mock-headers.enabled=true"
})
@DisplayName("Comprehensive Verification: 14/14 Purchase Order Lifecycle Remediations")
public class LifecycleRemediationComprehensiveTest {

    @Autowired
    private ComandaApplicationService comandaAppService;

    @Autowired
    private ComandaRepo comandaRepo;

    @Autowired
    private FurnizorRepo furnizorRepo;

    @Autowired
    private NotificariDispatcherService notificariDispatcher;

    @Autowired
    private MockMvc mockMvc;

    private IdFurnizor creeazaFurnizorAprobat() {
        IdFurnizor furnizorId = IdFurnizor.random();
        Furnizor f = new Furnizor(
                furnizorId,
                "RO" + System.currentTimeMillis(),
                "Furnizor Test Remediere SRL",
                null,
                Set.of(new Email("test@furnizor.ro")),
                new Iban("RO98BTRL0000000000000088"),
                Set.of("MAT-01"),
                null
        );
        f.trimiteLaEvaluare();
        f.aproba();
        furnizorRepo.add(f);
        return furnizorId;
    }

    private IdComanda creeazaComanda(IdFurnizor fId, double cantitate, double pret) {
        LinieComanda linie = new LinieComanda(
                CodMaterial.of("MAT-01"),
                Cantitate.of(cantitate, "BUC"),
                PretUnitar.of(pret, "RON")
        );
        CreeazaComandaCommand cmd = new CreeazaComandaCommand(
                "CMD-REM-" + UUID.randomUUID().toString().substring(0, 8),
                fId,
                UUID.randomUUID(),
                LocalDate.now(),
                LocalDate.now().plusDays(14),
                "Depozit Iasi",
                List.of(linie)
        );
        return comandaAppService.creeazaComanda(cmd);
    }

    @Test
    @DisplayName("Finding 1: Approval Workflow Advances Aggregate State to Aprobat and Persists Audit Metadata")
    void testFinding1_ApprovalAdvancesStateToAprobat() {
        IdFurnizor fId = creeazaFurnizorAprobat();
        IdComanda cId = creeazaComanda(fId, 50.0, 100.0); // 5,000 RON -> RESPONSABIL_ACHIZITII

        ComandaAprovizionare comandaInainte = comandaRepo.findById(cId).orElseThrow();
        assertEquals(StareComanda.Solicitat, comandaInainte.getStareComanda());

        // Aprobare
        comandaAppService.valideazaSiAproba(cId);

        ComandaAprovizionare comandaDupa = comandaRepo.findById(cId).orElseThrow();
        assertEquals(StareComanda.Aprobat, comandaDupa.getStareComanda(), "Starea trebuie sa fie Aprobat!");
        assertNotNull(comandaDupa.getDataAprobare(), "Data aprobarii trebuie sa fie persistata!");
        assertEquals("RESPONSABIL_ACHIZITII", comandaDupa.getNivelAprobare());

        // Aprobare duplicata trebuie sa arunce exceptie (idempotenta negativa)
        assertThrows(IllegalStateException.class, () -> comandaAppService.valideazaSiAproba(cId),
                "O comanda deja aprobata nu mai poate fi aprobata inca o data!");
    }

    @Test
    @DisplayName("Finding 2: Approval Bypass via Direct Confirmation is Strictly Blocked")
    void testFinding2_ApprovalBypassBlocked() {
        IdFurnizor fId = creeazaFurnizorAprobat();
        IdComanda cId = creeazaComanda(fId, 20.0, 50.0);

        // Incercare de confirmare direct din Solicitat -> Trebuie sa esueze!
        assertThrows(IllegalStateException.class, () ->
                comandaAppService.confirmaComanda(new ConfirmaComandaCommand(cId, LocalDate.now().plusDays(5))),
                "Comanda neaprobata nu poate fi confirmata direct de furnizor!");

        // Dupa aprobare -> Confirmarea reuseste
        comandaAppService.valideazaSiAproba(cId);
        assertDoesNotThrow(() ->
                comandaAppService.confirmaComanda(new ConfirmaComandaCommand(cId, LocalDate.now().plusDays(5))));

        ComandaAprovizionare c = comandaRepo.findById(cId).orElseThrow();
        assertEquals(StareComanda.Confirmat, c.getStareComanda());
    }

    @Test
    @DisplayName("Finding 3: Approval and Closure Operations Exposed in Presentation Layer (REST)")
    void testFinding3_ApprovalAndClosureExposedInRest() throws Exception {
        IdFurnizor fId = creeazaFurnizorAprobat();
        IdComanda cId = creeazaComanda(fId, 10.0, 100.0);

        // POST /api/comenzi/{id}/aprobare
        mockMvc.perform(post("/api/comenzi/" + cId.getValue() + "/aprobare")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ComandaAprovizionare c = comandaRepo.findById(cId).orElseThrow();
        assertEquals(StareComanda.Aprobat, c.getStareComanda());

        // Confirmare, Livrare si Receptie pentru a ajunge in Receptionat
        comandaAppService.confirmaComanda(new ConfirmaComandaCommand(cId, LocalDate.now().plusDays(5)));
        IdLot lotId = comandaAppService.planificaLot(cId, "LOT-REST", CodMaterial.of("MAT-01"), Cantitate.of(10.0, "BUC"), LocalDate.now(), LocalDate.now().plusYears(1));
        comandaAppService.inregistreazaExpediere(new InregistreazaExpediereCommand(cId, "FAN", "AWB-REST", Set.of(lotId)));
        comandaAppService.inregistreazaReceptie(new InregistreazaReceptieCommand(cId, lotId, Cantitate.of(10.0, "BUC")));

        // POST /api/comenzi/{id}/inchidere
        mockMvc.perform(post("/api/comenzi/" + cId.getValue() + "/inchidere")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ComandaAprovizionare cInchisa = comandaRepo.findById(cId).orElseThrow();
        assertEquals(StareComanda.Inchis, cInchisa.getStareComanda());
    }

    @Test
    @DisplayName("Finding 4: Missing Terminal State 'Inchis' (Reconciliation & Closure)")
    void testFinding4_TerminalStateInchis() {
        IdFurnizor fId = creeazaFurnizorAprobat();
        IdComanda cId = creeazaComanda(fId, 10.0, 100.0);

        comandaAppService.valideazaSiAproba(cId);
        comandaAppService.confirmaComanda(new ConfirmaComandaCommand(cId, LocalDate.now().plusDays(5)));

        IdLot lotId = comandaAppService.planificaLot(cId, "LOT-INCHIS", CodMaterial.of("MAT-01"), Cantitate.of(10.0, "BUC"), LocalDate.now(), LocalDate.now().plusYears(1));
        comandaAppService.inregistreazaExpediere(new InregistreazaExpediereCommand(cId, "FAN", "AWB-INCHIS", Set.of(lotId)));
        comandaAppService.inregistreazaReceptie(new InregistreazaReceptieCommand(cId, lotId, Cantitate.of(10.0, "BUC")));

        ComandaAprovizionare cReceptionata = comandaRepo.findById(cId).orElseThrow();
        assertEquals(StareComanda.Receptionat, cReceptionata.getStareComanda());

        // Inchidere comanda
        comandaAppService.inchideComanda(cId);

        ComandaAprovizionare cInchisa = comandaRepo.findById(cId).orElseThrow();
        assertEquals(StareComanda.Inchis, cInchisa.getStareComanda());
        assertNotNull(cInchisa.getDataInchidere());

        // Incercare de anulare a unei comenzi inchise -> Trebuie sa fie respinsa
        assertThrows(IllegalStateException.class, () ->
                comandaAppService.anuleazaComanda(new AnuleazaComandaCommand(cId)));
    }

    @Test
    @DisplayName("Finding 5: Quality Rejection Synchronizes Aggregate State (PO Demoted from Receptionat)")
    void testFinding5_QualityFeedbackSynchronized() {
        IdFurnizor fId = creeazaFurnizorAprobat();
        IdComanda cId = creeazaComanda(fId, 50.0, 100.0);

        comandaAppService.valideazaSiAproba(cId);
        comandaAppService.confirmaComanda(new ConfirmaComandaCommand(cId, LocalDate.now().plusDays(5)));

        IdLot lotId = comandaAppService.planificaLot(cId, "LOT-QA-1", CodMaterial.of("MAT-01"), Cantitate.of(50.0, "BUC"), LocalDate.now(), LocalDate.now().plusYears(1));
        comandaAppService.inregistreazaExpediere(new InregistreazaExpediereCommand(cId, "FAN", "AWB-QA-1", Set.of(lotId)));
        comandaAppService.inregistreazaReceptie(new InregistreazaReceptieCommand(cId, lotId, Cantitate.of(50.0, "BUC")));

        ComandaAprovizionare c = comandaRepo.findById(cId).orElseThrow();
        assertEquals(StareComanda.Receptionat, c.getStareComanda());

        // Inspectia AQL respinge lotul ca Neconform
        comandaAppService.aplicaVerdictInspectie(cId, lotId, RezultatCalitateLot.Neconform);

        ComandaAprovizionare cDupaRespingere = comandaRepo.findById(cId).orElseThrow();
        // Cantitatea primita utila a devenit 0 -> Comanda nu mai este acoperita integral si a fost retrogradata la ReceptionatPartial
        assertFalse(cDupaRespingere.esteAcoperitaIntegral());
        assertEquals(StareComanda.ReceptionatPartial, cDupaRespingere.getStareComanda(),
                "Starea comenzii trebuie sa reflecte neconformitatea si retrogradarea din Receptionat!");
    }

    @Test
    @DisplayName("Findings 6 & 7: Dead Events (ComandaCreata & ComandaAprobata) are Consumed by Notificari Subsystem")
    void testFindings6And7_DeadEventsConsumed() {
        notificariDispatcher.stergeIstoric();
        IdFurnizor fId = creeazaFurnizorAprobat();
        IdComanda cId = creeazaComanda(fId, 10.0, 200.0);

        // Verificare consum ComandaCreataPublicEvent
        var notificariCreare = notificariDispatcher.getNotificariPentruEntitate(cId.getValue());
        boolean hasCreata = notificariCreare.stream().anyMatch(n -> "COM_COMANDA_CREATA".equals(n.tipAlerta()));
        assertTrue(hasCreata, "ComandaCreataPublicEvent trebuie sa fie consumat si inregistrat in Notificari!");

        // Aprobare comanda
        comandaAppService.valideazaSiAproba(cId);

        // Verificare consum ComandaAprobataPublicEvent
        var notificariAprobare = notificariDispatcher.getNotificariPentruEntitate(cId.getValue());
        boolean hasAprobata = notificariAprobare.stream().anyMatch(n -> "COM_COMANDA_APROBATA".equals(n.tipAlerta()));
        assertTrue(hasAprobata, "ComandaAprobataPublicEvent trebuie sa fie consumat si inregistrat in Notificari!");
    }

    @Test
    @DisplayName("Findings 8 to 13: Missing Events Emitted and Consumed with Business Effects")
    void testFindings8To13_MissingEventsLifecycle() {
        notificariDispatcher.stergeIstoric();
        IdFurnizor fId = creeazaFurnizorAprobat();
        IdComanda cId = creeazaComanda(fId, 40.0, 100.0);

        comandaAppService.valideazaSiAproba(cId);

        // Finding 8: ComandaConfirmataPublicEvent
        comandaAppService.confirmaComanda(new ConfirmaComandaCommand(cId, LocalDate.now().plusDays(7)));
        assertTrue(notificariDispatcher.getNotificariPentruEntitate(cId.getValue()).stream()
                .anyMatch(n -> "COM_COMANDA_CONFIRMATA".equals(n.tipAlerta())), "ComandaConfirmataEvent trebuie sa fie emis si consumat!");

        // Finding 10: ComandaExpediataPublicEvent
        IdLot lotId = comandaAppService.planificaLot(cId, "LOT-EV-1", CodMaterial.of("MAT-01"), Cantitate.of(40.0, "BUC"), LocalDate.now(), LocalDate.now().plusYears(1));
        comandaAppService.inregistreazaExpediere(new InregistreazaExpediereCommand(cId, "DHL", "AWB-EV-1", Set.of(lotId)));
        assertTrue(notificariDispatcher.getNotificariPentruEntitate(cId.getValue()).stream()
                .anyMatch(n -> "COM_COMANDA_EXPEDIATA".equals(n.tipAlerta())), "ComandaExpediataEvent trebuie sa fie emis si consumat!");

        // Receptie
        comandaAppService.inregistreazaReceptie(new InregistreazaReceptieCommand(cId, lotId, Cantitate.of(40.0, "BUC")));

        // Finding 11: LotCarantinatPublicEvent
        comandaAppService.plaseazaLotInCarantina(cId, lotId);
        assertTrue(notificariDispatcher.getNotificariPentruEntitate(lotId.getValue()).stream()
                .anyMatch(n -> "COM_LOT_CARANTINAT".equals(n.tipAlerta())), "LotCarantinatEvent trebuie sa fie emis si consumat!");

        // Finding 12: LotRespinsPublicEvent
        comandaAppService.aplicaVerdictInspectie(cId, lotId, RezultatCalitateLot.Neconform);
        assertTrue(notificariDispatcher.getNotificariPentruEntitate(lotId.getValue()).stream()
                .anyMatch(n -> "COM_LOT_RESPINS".equals(n.tipAlerta())), "LotRespinsEvent trebuie sa fie emis si consumat!");

        // Finding 13: LotReturnatPublicEvent
        comandaAppService.returneazaLotFurnizorului(cId, lotId);
        assertTrue(notificariDispatcher.getNotificariPentruEntitate(lotId.getValue()).stream()
                .anyMatch(n -> "COM_LOT_RETURNAT".equals(n.tipAlerta())), "LotReturnatEvent trebuie sa fie emis si consumat!");

        // Finding 9: ComandaRefuzataPublicEvent (pe comanda noua)
        IdComanda cIdRefuz = creeazaComanda(fId, 10.0, 50.0);
        comandaAppService.refuzaComanda(cIdRefuz);
        assertTrue(notificariDispatcher.getNotificariPentruEntitate(cIdRefuz.getValue()).stream()
                .anyMatch(n -> "COM_COMANDA_REFUZATA".equals(n.tipAlerta())), "ComandaRefuzataEvent trebuie sa fie emis si consumat!");
    }

    @Test
    @DisplayName("Finding 14: Approval Policy Runtime Authorization Enforced by Thresholds")
    void testFinding14_ApprovalPolicyEnforcedAtRuntime() {
        IdFurnizor fId = creeazaFurnizorAprobat();

        // Comanda de 75,000 RON (> 50,000 RON -> Nivel Necesar: DIRECTOR_GENERAL)
        IdComanda cIdMare = creeazaComanda(fId, 750.0, 100.0);

        // Responsabil Achizitii incearca aprobare -> ESEC cu SecurityException
        assertThrows(SecurityException.class, () ->
                comandaAppService.valideazaSiAproba(cIdMare, UUID.randomUUID(), NivelAprobare.RESPONSABIL_ACHIZITII),
                "Responsabil Achizitii nu are voie sa aprobe comenzi > 50,000 RON!");

        // Manager Departament incearca aprobare -> ESEC cu SecurityException
        assertThrows(SecurityException.class, () ->
                comandaAppService.valideazaSiAproba(cIdMare, UUID.randomUUID(), NivelAprobare.MANAGER_DEPARTAMENT),
                "Manager Departament nu are voie sa aprobe comenzi > 50,000 RON!");

        // Director General incearca aprobare -> SUCCES
        assertDoesNotThrow(() ->
                comandaAppService.valideazaSiAproba(cIdMare, UUID.randomUUID(), NivelAprobare.DIRECTOR_GENERAL));

        ComandaAprovizionare c = comandaRepo.findById(cIdMare).orElseThrow();
        assertEquals(StareComanda.Aprobat, c.getStareComanda());
        assertEquals("DIRECTOR_GENERAL", c.getNivelAprobare());
    }
}
