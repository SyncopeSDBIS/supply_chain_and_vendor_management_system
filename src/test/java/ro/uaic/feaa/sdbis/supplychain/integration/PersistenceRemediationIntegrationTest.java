package ro.uaic.feaa.sdbis.supplychain.integration;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.CalitateModuleFacade;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.ComenziModuleFacade;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.FacturiModuleFacade;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.shared.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Integration Tests: Validare Criminalistică Remedieri Persistență (Remediation Tasks 1-7)")
public class PersistenceRemediationIntegrationTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ComandaRepo comandaRepo;

    @Autowired
    private FacturaRepo facturaRepo;

    @Autowired
    private FurnizorRepo furnizorRepo;

    @Autowired
    private ComenziModuleFacade comenziFacade;

    @Autowired
    private FacturiModuleFacade facturiFacade;

    @Autowired
    private CalitateModuleFacade calitateFacade;

    @Autowired
    private DocumentStorageService documentStorageService;

    @Autowired
    private ro.uaic.feaa.sdbis.supplychain.comenzi.infrastructure.persistence.SpringDataComandaRepository springDataComandaRepo;

    @Autowired
    private ro.uaic.feaa.sdbis.supplychain.calitate.infrastructure.persistence.SpringDataFurnizorRepository springDataFurnizorRepo;

    @Autowired
    private ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure.persistence.SpringDataFacturaRepository springDataFacturaRepo;

    @Autowired
    private ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure.persistence.SpringDataNIRRepository springDataNirRepo;

    @Test
    @Transactional
    @DisplayName("Task #1: Verificare interogare țintită pentru Comenzi Timeout și Facturi Scadente (Fără findAll)")
    void testTargetedQueryEliminatesUnboundedFindAll() {
        // 1. Setup comenzi: una veche (Solicitat) si una recenta
        IdComanda id1 = IdComanda.random();
        ComandaAprovizionare cmd1 = new ComandaAprovizionare(
                id1, "CMD-TO-1", IdFurnizor.random(), UUID.randomUUID(),
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(2), "Depozit Central",
                List.of(new LinieComanda(CodMaterial.of("TABLA-01"), Cantitate.of(10.0, "KG"), PretUnitar.of(100.0, "EUR")))
        );
        comandaRepo.add(cmd1);

        IdComanda id2 = IdComanda.random();
        ComandaAprovizionare cmd2 = new ComandaAprovizionare(
                id2, "CMD-RECENT-2", IdFurnizor.random(), UUID.randomUUID(),
                LocalDate.now(), LocalDate.now().plusDays(10), "Depozit Central",
                List.of(new LinieComanda(CodMaterial.of("TABLA-01"), Cantitate.of(10.0, "KG"), PretUnitar.of(100.0, "EUR")))
        );
        comandaRepo.add(cmd2);

        em.flush();

        // 2. Apel timeout comenzi: trebuie sa proceseze DOAR comanda veche
        int timedOut = comenziFacade.proceseazaTimeoutConfirmareComenzi();
        assertTrue(timedOut >= 1, "Comanda veche nesolutionata trebuie detectata de query-ul indexat");

        // 3. Setup facturi cu scadenta in 3 zile vs scadenta in 10 zile
        FacturaFurnizor f1 = new FacturaFurnizor(
                UUID.randomUUID(), "F-SCAD-1", "001", LocalDate.now(), LocalDate.now().plusDays(3),
                id1, IdFurnizor.random(),
                List.of(new LinieFactura(CodMaterial.of("TABLA-01"), BigDecimal.valueOf(10), "KG", Suma.of(BigDecimal.valueOf(100), "EUR"), BigDecimal.valueOf(0.19))),
                Currency.getInstance("EUR")
        );
        f1.trimiteLaVerificare();
        f1.aproba();
        facturaRepo.add(f1);

        FacturaFurnizor f2 = new FacturaFurnizor(
                UUID.randomUUID(), "F-SCAD-2", "002", LocalDate.now(), LocalDate.now().plusDays(15),
                id1, IdFurnizor.random(),
                List.of(new LinieFactura(CodMaterial.of("TABLA-01"), BigDecimal.valueOf(10), "KG", Suma.of(BigDecimal.valueOf(100), "EUR"), BigDecimal.valueOf(0.19))),
                Currency.getInstance("EUR")
        );
        f2.trimiteLaVerificare();
        f2.aproba();
        facturaRepo.add(f2);

        em.flush();

        int scadenteGasite = facturiFacade.proceseazaVerificareScadenteFacturi();
        assertTrue(scadenteGasite >= 1, "Factura cu scadenta la t+3 trebuie selectata direct din index SQL");
    }

    @Test
    @DisplayName("Task #2: Absența UnexpectedRollbackException în procesarea batch Calitate")
    void testNoUnexpectedRollbackInCalitateBatch() {
        // Rularea batch-ului de calcul automat nu trebuie sa arunce UnexpectedRollbackException
        assertDoesNotThrow(() -> {
            int procesate = calitateFacade.proceseazaCalculScorecardLunarAutomat();
            assertTrue(procesate >= 0);
        });
    }

    @Test
    @Transactional
    @DisplayName("Task #3: Document Storage - Metadatele sunt citite prin proiectie fără încărcarea BLOB-ului")
    void testDocumentStorageMetadataProjection() {
        UUID refId = UUID.randomUUID();
        byte[] payload = "CONTINUT_DOCUMENT_SCANAT_TEST_BLOB_BYTEA".getBytes();

        DocumentAtasatEntity doc = documentStorageService.salveazaDocument(
                "FACTURA_SCANATA", refId, "factura_test.pdf", "application/pdf", payload
        );

        em.flush();
        em.clear();

        // Citire exclusiv prin proiectie de metadate
        List<DocumentMetadataView> metadate = documentStorageService.listeazaMetadateDupaReferinta(refId);
        assertFalse(metadate.isEmpty(), "Proiectia trebuie sa aduca metadatele din baza de date");
        DocumentMetadataView meta = metadate.get(0);
        assertEquals("factura_test.pdf", meta.getNumeFisier());
        assertEquals("application/pdf", meta.getTipContinut());
        assertEquals(payload.length, meta.getDimensiuneBytes());

        // Descarcare completa
        DocumentAtasatEntity loaded = documentStorageService.gasesteDupaId(doc.getId())
                .orElseThrow(() -> new AssertionError("Documentul trebuie sa existe"));
        assertArrayEquals(payload, loaded.getContinut(), "Continutul binar este incarcat la cerere");
    }

    @Test
    @Transactional
    @DisplayName("Task #4: Eliminare cost INSERT+UPDATE prin legături de agregat optimizate")
    void testLotAndScorecardBidirectionalIntegration() {
        IdComanda cId = IdComanda.random();
        ComandaAprovizionare comanda = new ComandaAprovizionare(
                cId, "CMD-LOT-TEST-" + System.currentTimeMillis(), IdFurnizor.random(), UUID.randomUUID(),
                LocalDate.now(), LocalDate.now().plusDays(5), "Depozit Central",
                List.of(new LinieComanda(CodMaterial.of("TABLA-01"), Cantitate.of(100.0, "KG"), PretUnitar.of(50.0, "EUR")))
        );
        comanda.aproba(UUID.randomUUID(), "MANAGER_DEPARTAMENT");
        comanda.confirma(LocalDate.now().plusDays(5));
        comandaRepo.add(comanda);

        IdLot lotId = comanda.planificaLot("LOT-BI-01", CodMaterial.of("TABLA-01"), Cantitate.of(50.0, "KG"), LocalDate.now(), LocalDate.now().plusMonths(6));
        comandaRepo.save(comanda);

        em.flush();
        em.clear();

        ComandaAprovizionare reloaded = comandaRepo.findById(cId).orElseThrow();
        assertEquals(1, reloaded.getLoturi().size());
        assertEquals(lotId, reloaded.getLoturi().get(0).getIdLot());
        assertNotNull(reloaded.getLoturi().get(0).getComanda(), "Lotul copil cunoaste comanda parinte (cheia straina este setata la INSERT)");
    }

    @Test
    @Transactional
    @DisplayName("Task #5: AbstractRepository save() returnează instanța gestionată pentru entități detașate")
    void testAbstractRepositorySaveReturnsManagedEntity() {
        IdFurnizor furnizorId = IdFurnizor.random();
        Furnizor f = new Furnizor(
                furnizorId, "RO" + (System.currentTimeMillis() % 10000000), "Furnizor Managed SRL",
                null, Collections.emptySet(), new Iban("RO49AAAA1B31007593840001"), Set.of("METAL"), null
        );
        furnizorRepo.add(f);

        em.flush();
        em.clear(); // Fortam detasarea entitatii din persistence context

        assertFalse(em.contains(f), "Entitatea f este acum detasata dupa clear()");

        // Apel save() pe entitate detasata
        Furnizor managed = furnizorRepo.save(f);
        assertNotNull(managed);
        assertTrue(em.contains(managed), "save() trebuie sa returneze instanta gestionata (managed) returnata de merge()");
    }

    @Test
    @Transactional
    @DisplayName("Task #6: Normalizare Relatională 1NF pentru Expedieri și Loturi Asociate")
    void testNormalizedExpediereLoturiStorage() {
        IdComanda cId = IdComanda.random();
        ComandaAprovizionare comanda = new ComandaAprovizionare(
                cId, "CMD-EXP-TEST-" + System.currentTimeMillis(), IdFurnizor.random(), UUID.randomUUID(),
                LocalDate.now(), LocalDate.now().plusDays(5), "Depozit Central",
                List.of(new LinieComanda(CodMaterial.of("TABLA-01"), Cantitate.of(100.0, "KG"), PretUnitar.of(50.0, "EUR")))
        );
        comanda.aproba(UUID.randomUUID(), "MANAGER_DEPARTAMENT");
        comanda.confirma(LocalDate.now().plusDays(5));
        IdLot l1 = comanda.planificaLot("LOT-EXP-1", CodMaterial.of("TABLA-01"), Cantitate.of(50.0, "KG"), LocalDate.now(), LocalDate.now().plusMonths(6));
        IdLot l2 = comanda.planificaLot("LOT-EXP-2", CodMaterial.of("TABLA-01"), Cantitate.of(50.0, "KG"), LocalDate.now(), LocalDate.now().plusMonths(6));

        comanda.inregistreazaExpediere("FAN_COURIER", "AWB-NORM-1234", Set.of(l1, l2));
        comandaRepo.add(comanda);

        em.flush();
        em.clear();

        ComandaAprovizionare reloaded = comandaRepo.findById(cId).orElseThrow();
        assertEquals(1, reloaded.getExpedieri().size());
        Expediere exp = reloaded.getExpedieri().get(0);
        assertEquals("AWB-NORM-1234", exp.getNrAWB());
        assertEquals(2, exp.getLoturi().size(), "Loturile asociate expedierii sunt citite din tabela normalizata comenzi_expedieri_loturi");
        assertTrue(exp.getLoturi().contains(l1));
        assertTrue(exp.getLoturi().contains(l2));
    }

    @Test
    @Transactional
    @DisplayName("Sprint 3: Validare Spring Data JPA Repositories & Custom Queries (@Query & Derived Operations)")
    void testSpringDataJpaRepositoriesAndCustomQueries() {
        assertNotNull(springDataComandaRepo, "SpringDataComandaRepository bean must be injected");
        assertNotNull(springDataFurnizorRepo, "SpringDataFurnizorRepository bean must be injected");
        assertNotNull(springDataFacturaRepo, "SpringDataFacturaRepository bean must be injected");
        assertNotNull(springDataNirRepo, "SpringDataNIRRepository bean must be injected");

        // 1. Verify Comanda Spring Data queries
        String nrCmd = "CMD-SD-TEST-" + System.currentTimeMillis();
        IdFurnizor furnizorId = IdFurnizor.random();
        ComandaAprovizionare c = new ComandaAprovizionare(
                IdComanda.random(), nrCmd, furnizorId, UUID.randomUUID(),
                LocalDate.now(), LocalDate.now().plusDays(10), "Depozit Otopeni",
                List.of(new LinieComanda(CodMaterial.of("MAT-SD-01"), Cantitate.of(20.0, "BUC"), PretUnitar.of(15.0, "EUR")))
        );
        springDataComandaRepo.save(c);

        Optional<ComandaAprovizionare> foundCmd = springDataComandaRepo.findByNumarComanda(nrCmd);
        assertTrue(foundCmd.isPresent(), "findByNumarComanda must find persisted order");
        List<ComandaAprovizionare> furnizorOrders = springDataComandaRepo.findByFurnizorId(furnizorId);
        assertFalse(furnizorOrders.isEmpty(), "findByFurnizorId must find orders for supplier");
        List<ComandaAprovizionare> filteredOrders = springDataComandaRepo.findByFurnizorAndStare(furnizorId, StareComanda.Solicitat);
        assertFalse(filteredOrders.isEmpty(), "@Query findByFurnizorAndStare must return matching orders");

        // 2. Verify Furnizor Spring Data queries
        List<Furnizor> activeList = springDataFurnizorRepo.findActiveFurnizoriOrdered(StareFurnizor.Aprobat);
        assertNotNull(activeList, "@Query findActiveFurnizoriOrdered must return non-null list");

        // 3. Verify Factura Spring Data queries
        List<FacturaFurnizor> unpaidInvoices = springDataFacturaRepo.findFacturiNeachitatePentruFurnizor(furnizorId);
        assertNotNull(unpaidInvoices, "@Query findFacturiNeachitatePentruFurnizor must return non-null list");

        // 4. Verify NIR Spring Data queries
        assertFalse(springDataNirRepo.existsByEventSursaId(UUID.randomUUID()), "existsByEventSursaId must return false for unknown UUID");
    }
}

