package ro.uaic.feaa.sdbis.supplychain.integration;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.CalitateApplicationService;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.command.AlocaInspectorCommand;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.command.IncepeInspectieCommand;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.command.InregistreazaVerdictCommand;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.query.CalitateQueryService;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Adresa;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.ConditiiContractuale;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Email;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Furnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.FurnizorRepo;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Iban;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.PlanAQL;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.FacturaApplicationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.CreeazaOrdinPlataCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.InregistreazaFacturaCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.TransmiteOrdinCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.query.FacturaQueryService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaRepo;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.LinieFactura;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.OrdinPlata;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.OrdinPlataRepo;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareOrdinPlata;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.Suma;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("Integration Tests: Persistență Reală Referință Bancară, Defecte Găsite și Audit Fields")
public class OrdinPlataPersistenceIntegrationTest {

    @Autowired
    private FacturaApplicationService facturaAppService;

    @Autowired
    private FacturaQueryService facturaQueryService;

    @Autowired
    private FacturaRepo facturaRepo;

    @Autowired
    private OrdinPlataRepo ordinPlataRepo;

    @Autowired
    private CalitateApplicationService calitateAppService;

    @Autowired
    private CalitateQueryService calitateQueryService;

    @Autowired
    private FurnizorRepo furnizorRepo;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("Test Persistență Reală: referinta_bancara este salvată în DB și persistă după commit/evict")
    void testBankReferenceActuallyPersistedInDb() {
        IdFurnizor furnizorId = IdFurnizor.random();
        Furnizor f = new Furnizor(furnizorId, "RO" + (System.currentTimeMillis() % 10000000), "Furnizor Plati SRL",
                null, Collections.emptySet(), new Iban("RO49AAAA1B31007593840099"), Collections.singleton("MAT"), null);
        f.trimiteLaEvaluare();
        f.aproba();
        furnizorRepo.add(f);

        LinieFactura lf = new LinieFactura(CodMaterial.of("M1"), BigDecimal.valueOf(100), "BUC", Suma.of(50.0, "EUR"), BigDecimal.valueOf(0.19));
        UUID facturaId = facturaAppService.inregistreazaFactura(new InregistreazaFacturaCommand(
                "FC-BNK", "999", LocalDate.now(), LocalDate.now().plusDays(30),
                IdComanda.random(), furnizorId,
                List.of(lf), Currency.getInstance("EUR")
        ));

        // Aprobare factura conform regulilor de domeniu pentru a permite plata
        FacturaFurnizor fFact = facturaRepo.findById(facturaId).orElseThrow();
        fFact.trimiteLaVerificare();
        fFact.aproba();
        facturaRepo.save(fFact);

        UUID ordinId = facturaAppService.creeazaOrdinPlata(new CreeazaOrdinPlataCommand(
                facturaId, Suma.of(5950.0, "EUR"), UUID.randomUUID()
        ));

        // Transmitere la banca prin gateway
        facturaAppService.transmiteOrdin(new TransmiteOrdinCommand(
                ordinId, "RO49AAAA1B31007593840099", null
        ));

        // Evict L1 Hibernate Cache pentru a forta citirea directa din baza de date
        em.flush();
        em.clear();

        // 1. Verificare directa prin repository
        OrdinPlata ordinDB = ordinPlataRepo.findById(ordinId)
                .orElseThrow(() -> new AssertionError("Ordinul de plata nu a fost gasit in baza de date!"));

        assertEquals(StareOrdinPlata.TransmisLaBanca, ordinDB.getStareOrdin(), "Starea trebuie sa fie TransmisLaBanca");
        assertNotNull(ordinDB.getReferintaBancara(), "CRITIC: referinta_bancara TREBUIE sa fie persistata in baza de date (nu NULL)!");
        assertTrue(ordinDB.getReferintaBancara().startsWith("pi_"), "Referinta bancara trebuie sa inceapa cu prefixul Stripe 'pi_'");

        // 2. Verificare campuri audit
        assertNotNull(ordinDB.getCreatedAt(), "createdAt nu trebuie sa fie null");
        assertNotNull(ordinDB.getUpdatedAt(), "updatedAt nu trebuie sa fie null");

        // 3. Verificare prin Query Service (Read Model)
        var view = facturaQueryService.obtineDetaliiOrdinPlata(ordinId)
                .orElseThrow(() -> new AssertionError("View-ul CQRS nu a returnat ordinul"));

        assertEquals(ordinDB.getReferintaBancara(), view.referintaBancara(), "View-ul CQRS trebuie sa expuna referinta bancara persistata");
        assertEquals(StareOrdinPlata.TransmisLaBanca, view.stareOrdin());
    }

    @Test
    @DisplayName("Test Persistență Defecte Găsite: defecte_gasite este salvat în DB și expus în View-ul CQRS")
    void testInspectieDefecteGasitePersistedAndReadable() {
        IdFurnizor furnizorId = calitateAppService.inregistreazaFurnizor(
                "RO" + (System.currentTimeMillis() % 100000000),
                "Furnizor Test Calitate SRL",
                new Adresa("Strada Fabricii", "10", "Iasi", "700505"),
                Set.of(new Email("calitate@furnizor.ro")),
                Iban.of("RO98BTRL0000000000000001"),
                Set.of("METALURGIE"),
                new ConditiiContractuale(30, Currency.getInstance("RON"), Collections.emptyMap())
        );
        IdComanda comandaId = IdComanda.random();
        IdLot lotId = IdLot.random();

        UUID inspectieId = calitateAppService.programeazaInspectie(
                lotId, comandaId, furnizorId, CodMaterial.of("TABLA-01"),
                PlanAQL.standard(4.0, 20, 1),
                Perioada.of(LocalDate.now(), LocalDate.now().plusDays(2))
        );

        UUID inspectorId = calitateAppService.creeazaInspector("Ion", "Popescu");
        calitateAppService.adaugaCertificareInspector(inspectorId, "ISO-9001", LocalDate.now().plusMonths(6));
        calitateAppService.declaraDisponibilitateInspector(inspectorId, LocalDate.now().minusDays(1), LocalDate.now().plusDays(10));

        calitateAppService.alocaInspector(new AlocaInspectorCommand(inspectieId, inspectorId));
        calitateAppService.incepeInspectie(new IncepeInspectieCommand(inspectieId));

        calitateAppService.inregistreazaVerdict(new InregistreazaVerdictCommand(inspectieId, 5));

        em.flush();
        em.clear();

        var inspectieView = calitateQueryService.obtineDetaliiInspectie(inspectieId)
                .orElseThrow(() -> new AssertionError("Inspectia nu a fost gasita in CQRS"));

        assertEquals(5, inspectieView.defecteGasite(), "defecteGasite trebuie sa fie citit din DB cu valoarea 5");
    }
}
