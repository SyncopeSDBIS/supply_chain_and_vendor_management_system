package ro.uaic.feaa.sdbis.supplychain.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.event.ComandaAprobataPublicEvent;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.ComandaApplicationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.ConfirmaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.CreeazaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.policy.PoliticaAprobareComanda.NivelAprobare;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.OutboxRepo;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("Remediation Verification: Genuinely Functional Order Approval & Invariants Workflow")
public class ComandaWorkflowRemediationTest {

    @Autowired
    private ComandaApplicationService comandaAppService;

    @Autowired
    private ComandaRepo comandaRepo;

    @Autowired
    private FurnizorRepo furnizorRepo;

    @Autowired
    private OutboxRepo outboxRepo;

    @Test
    @DisplayName("valideazaSiAproba() valideaza politicile de aprobare, invarianții DDD si emite ComandaAprobataPublicEvent in Outbox")
    void testGenuineOrderApprovalWorkflow() {
        // 1. Furnizor eligibil aprobat
        IdFurnizor furnizorId = IdFurnizor.random();
        Furnizor furnizor = new Furnizor(
                furnizorId,
                "RO" + System.currentTimeMillis(),
                "Furnizor Strategic Acelor SRL",
                null,
                Set.of(new Email("procurement@acelor.ro")),
                new Iban("RO98BTRL0000000000000099"),
                Set.of("TABLA-01"),
                null
        );
        furnizor.trimiteLaEvaluare();
        furnizor.aproba();
        furnizorRepo.add(furnizor);

        // 2. Creare comanda in stare Solicitat
        LinieComanda linie = new LinieComanda(
                CodMaterial.of("TABLA-01"),
                Cantitate.of(200.0, "BUC"),
                PretUnitar.of(150.0, "RON") // Total = 30,000 RON -> Nivel Aprobare: MANAGER_DEPARTAMENT
        );
        CreeazaComandaCommand cmd = new CreeazaComandaCommand(
                "CMD-APROB-TEST-01",
                furnizorId,
                UUID.randomUUID(),
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                "Depozit Central",
                List.of(linie)
        );
        IdComanda comandaId = comandaAppService.creeazaComanda(cmd);

        // 3. Executie aprobare reala
        assertDoesNotThrow(() -> comandaAppService.valideazaSiAproba(comandaId));

        ComandaAprovizionare comandaSalvata = comandaRepo.findById(comandaId).orElseThrow();
        assertEquals(StareComanda.Aprobat, comandaSalvata.getStareComanda(), "Starea comenzii trebuie sa avanseze la Aprobat!");
        assertNotNull(comandaSalvata.getDataAprobare(), "Data aprobarii trebuie sa fie persistata!");
        assertNotNull(comandaSalvata.getAprobatDe(), "Identificatorul aprobatorului trebuie sa fie persistat!");
        assertEquals("MANAGER_DEPARTAMENT", comandaSalvata.getNivelAprobare());

        // 4. Verificare eveniment in Outbox
        List<DomainEvent> events = outboxRepo.pollUnpublishedEvents();
        boolean hasApprovalEvent = events.stream()
                .anyMatch(e -> e instanceof ComandaAprobataPublicEvent ce
                        && ce.comandaId().equals(comandaId.getValue())
                        && "MANAGER_DEPARTAMENT".equals(ce.nivelAprobare()));
        assertTrue(hasApprovalEvent, "Outbox trebuie sa contina ComandaAprobataPublicEvent cu nivelul de aprobare corect!");
    }

    @Test
    @DisplayName("confirma() este imposibila inainte de aprobare (Solicitat) si reuseste dupa aprobare")
    void testConfirmationRequiresApprovedState() {
        IdFurnizor furnizorId = IdFurnizor.random();
        Furnizor furnizor = new Furnizor(
                furnizorId,
                "RO" + System.currentTimeMillis(),
                "Furnizor Test Confirmare SRL",
                null,
                Set.of(new Email("test@confirmare.ro")),
                new Iban("RO98BTRL0000000000000077"),
                Set.of("TABLA-01"),
                null
        );
        furnizor.trimiteLaEvaluare();
        furnizor.aproba();
        furnizorRepo.add(furnizor);

        CreeazaComandaCommand cmd = new CreeazaComandaCommand(
                "CMD-CONFIRM-TEST-01",
                furnizorId,
                UUID.randomUUID(),
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                "Depozit Central",
                List.of(new LinieComanda(CodMaterial.of("TABLA-01"), Cantitate.of(50.0, "BUC"), PretUnitar.of(100.0, "RON")))
        );
        IdComanda comandaId = comandaAppService.creeazaComanda(cmd);

        // Confirmarea pe comanda neaprobata (in Solicitat) trebuie sa esueze
        assertThrows(IllegalStateException.class, () ->
                comandaAppService.confirmaComanda(new ConfirmaComandaCommand(
                        comandaId, LocalDate.now().plusDays(5))),
                "Confirmarea directa fara aprobare prealabila trebuie sa fie strict interzisa!");

        // Dupa aprobare -> Confirmarea reuseste
        comandaAppService.valideazaSiAproba(comandaId);
        assertDoesNotThrow(() ->
                comandaAppService.confirmaComanda(new ConfirmaComandaCommand(
                        comandaId, LocalDate.now().plusDays(5))));

        ComandaAprovizionare dupaConfirmare = comandaRepo.findById(comandaId).orElseThrow();
        assertEquals(StareComanda.Confirmat, dupaConfirmare.getStareComanda());
    }

    @Test
    @DisplayName("PoliticaAprobareComanda.poateAproba() arunca SecurityException daca nivelul este insuficient")
    void testApprovalPolicyEnforcesThresholds() {
        IdFurnizor furnizorId = IdFurnizor.random();
        Furnizor furnizor = new Furnizor(
                furnizorId,
                "RO" + System.currentTimeMillis(),
                "Furnizor Strategic Prag SRL",
                null,
                Set.of(new Email("prag@furnizor.ro")),
                new Iban("RO98BTRL0000000000000076"),
                Set.of("TABLA-01"),
                null
        );
        furnizor.trimiteLaEvaluare();
        furnizor.aproba();
        furnizorRepo.add(furnizor);

        // Comanda de 60,000 RON (> 50,000 RON -> Nivel necesar: DIRECTOR_GENERAL)
        CreeazaComandaCommand cmd = new CreeazaComandaCommand(
                "CMD-PRAG-TEST-01",
                furnizorId,
                UUID.randomUUID(),
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                "Depozit Central",
                List.of(new LinieComanda(CodMaterial.of("TABLA-01"), Cantitate.of(600.0, "BUC"), PretUnitar.of(100.0, "RON")))
        );
        IdComanda comandaId = comandaAppService.creeazaComanda(cmd);

        // Responsabil Achizitii (rang 1) incearca aprobarea unei comenzi de 60,000 RON -> SecurityException
        assertThrows(SecurityException.class, () ->
                comandaAppService.valideazaSiAproba(comandaId, UUID.randomUUID(),
                        NivelAprobare.RESPONSABIL_ACHIZITII));

        // Manager Departament (rang 2) incearca aprobarea unei comenzi de 60,000 RON -> SecurityException
        assertThrows(SecurityException.class, () ->
                comandaAppService.valideazaSiAproba(comandaId, UUID.randomUUID(),
                        NivelAprobare.MANAGER_DEPARTAMENT));

        // Director General (rang 3) aproba comanda -> SUCCES
        UUID directorId = UUID.randomUUID();
        assertDoesNotThrow(() ->
                comandaAppService.valideazaSiAproba(comandaId, directorId,
                        NivelAprobare.DIRECTOR_GENERAL));

        ComandaAprovizionare c = comandaRepo.findById(comandaId).orElseThrow();
        assertEquals(StareComanda.Aprobat, c.getStareComanda());
        assertEquals("DIRECTOR_GENERAL", c.getNivelAprobare());
        assertEquals(directorId, c.getAprobatDe());
    }

    @Test
    @DisplayName("valideazaSiAproba() respinge comanda daca furnizorul este suspendat sau neeligibil")
    void testApprovalFailsWhenSupplierIneligible() {
        // Furnizor candidat (neaprobat)
        IdFurnizor furnizorId = IdFurnizor.random();
        Furnizor furnizor = new Furnizor(
                furnizorId,
                "RO" + System.currentTimeMillis(),
                "Furnizor Candidat SRL",
                null,
                Set.of(new Email("cand@test.ro")),
                new Iban("RO98BTRL0000000000000098"),
                Set.of("TABLA-01"),
                null
        );
        furnizorRepo.add(furnizor);

        // Fortare salvare directa comanda cu furnizor candidat
        IdComanda comandaId = IdComanda.random();
        ComandaAprovizionare comanda = new ComandaAprovizionare(
                comandaId,
                "CMD-FAIL-APROB-01",
                furnizorId,
                UUID.randomUUID(),
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                "Depozit",
                Collections.singletonList(new LinieComanda(CodMaterial.of("TABLA-01"), Cantitate.of(10.0, "BUC"), PretUnitar.of(100.0, "RON")))
        );
        comandaRepo.add(comanda);

        // Aprobarea trebuie sa arunce exceptie de business
        assertThrows(IllegalStateException.class, () -> comandaAppService.valideazaSiAproba(comandaId));
    }
}

