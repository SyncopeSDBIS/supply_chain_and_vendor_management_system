package ro.uaic.feaa.sdbis.supplychain.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.*;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.factory.FurnizorFactory;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.factory.InspectieCalitateFactory;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.policy.PoliticaSelectieAQL;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.validation.CalitateValidationService;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.validation.ICalitateValidationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.factory.ComandaFactory;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.policy.PoliticaAprobareComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.validation.ComandaValidationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.validation.IComandaValidationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.factory.FacturaFactory;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.factory.NIRFactory;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.policy.PoliticaAutorizarePlata;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.validation.FacturaValidationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.validation.IFacturaValidationService;
import ro.uaic.feaa.sdbis.supplychain.shared.*;
import ro.uaic.feaa.sdbis.supplychain.shared.exception.DomainValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Sprint 2 - Validation Services, Bean Validation & Business Policies Tests (ADR-003)")
public class ValidationServicesTest {

    private IComandaValidationService comandaValidationService;
    private ICalitateValidationService calitateValidationService;
    private IFacturaValidationService facturaValidationService;
    private Validator validator;
    private PoliticaAprobareComanda politicaAprobareComanda;
    private PoliticaSelectieAQL politicaSelectieAQL;
    private PoliticaAutorizarePlata politicaAutorizarePlata;

    @BeforeEach
    void setUp() {
        comandaValidationService = new ComandaValidationService();
        calitateValidationService = new CalitateValidationService();
        facturaValidationService = new FacturaValidationService();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        politicaAprobareComanda = new PoliticaAprobareComanda();
        politicaSelectieAQL = new PoliticaSelectieAQL();
        politicaAutorizarePlata = new PoliticaAutorizarePlata();
    }

    // =========================================================================
    // 1. ComandaValidationService & ComandaFactory Tests
    // =========================================================================

    @Test
    @DisplayName("ComandaFactory & Validation: Scenariu valid comanda aprovizionare")
    void testComandaValidation_ValidScenario() {
        LinieComanda linie = new LinieComanda(
            CodMaterial.of("MAT-001"),
            Cantitate.of(50.0, "BUC"),
            PretUnitar.of(120.0, "RON")
        );

        ComandaAprovizionare comanda = ComandaFactory.creeaza(
            "CMD-2026-VAL-01",
            IdFurnizor.random(),
            UUID.randomUUID(),
            LocalDate.now(),
            LocalDate.now().plusDays(10),
            "DEPOZIT-CENTRAL",
            List.of(linie)
        );

        assertNotNull(comanda);
        assertDoesNotThrow(() -> comandaValidationService.valideazaComanda(comanda, true));
    }

    @Test
    @DisplayName("ComandaValidation: Scenariu invalid - furnizor neeligibil/neaprobat")
    void testComandaValidation_FurnizorNeeligibil() {
        LinieComanda linie = new LinieComanda(
            CodMaterial.of("MAT-001"),
            Cantitate.of(50.0, "BUC"),
            PretUnitar.of(120.0, "RON")
        );

        ComandaAprovizionare comanda = ComandaFactory.creeaza(
            "CMD-2026-VAL-02",
            IdFurnizor.random(),
            UUID.randomUUID(),
            LocalDate.now(),
            LocalDate.now().plusDays(10),
            "DEPOZIT-CENTRAL",
            List.of(linie)
        );

        DomainValidationException ex = assertThrows(DomainValidationException.class, () ->
            comandaValidationService.valideazaComanda(comanda, false)
        );
        assertEquals("FURNIZOR_NEELIGIBIL", ex.getCodEroare());
    }

    @Test
    @DisplayName("ComandaFactory: Scenariu invalid - data livrare ceruta anterioara datei comenzii")
    void testComandaFactory_DataLivrareInvalida() {
        LinieComanda linie = new LinieComanda(
            CodMaterial.of("MAT-001"),
            Cantitate.of(10.0, "BUC"),
            PretUnitar.of(100.0, "RON")
        );

        assertThrows(IllegalArgumentException.class, () ->
            ComandaFactory.creeaza(
                "CMD-INVALID-DATA",
                IdFurnizor.random(),
                UUID.randomUUID(),
                LocalDate.now(),
                LocalDate.now().minusDays(2),
                "DEPOZIT-CENTRAL",
                List.of(linie)
            )
        );
    }

    @Test
    @DisplayName("ComandaValidation: Scenariu invalid - tranzitie interzisa dupa anulare")
    void testComandaValidation_TranzitieDupaAnulare() {
        LinieComanda linie = new LinieComanda(
            CodMaterial.of("MAT-001"),
            Cantitate.of(10.0, "BUC"),
            PretUnitar.of(100.0, "RON")
        );
        ComandaAprovizionare comanda = ComandaFactory.creeaza(
            "CMD-2026-TR",
            IdFurnizor.random(),
            UUID.randomUUID(),
            LocalDate.now(),
            LocalDate.now().plusDays(5),
            "DEPOZIT-CENTRAL",
            List.of(linie)
        );

        comanda.anuleazaComanda();

        DomainValidationException ex = assertThrows(DomainValidationException.class, () ->
            comandaValidationService.valideazaTranzitieStare(comanda, StareComanda.Confirmat)
        );
        assertEquals("TRANZITIE_INTERZISA", ex.getCodEroare());
    }

    // =========================================================================
    // 2. CalitateValidationService & Furnizor / Inspectie Factories Tests
    // =========================================================================

    @Test
    @DisplayName("FurnizorFactory & CalitateValidation: Scenariu valid creare si validare furnizor")
    void testFurnizorValidation_ValidScenario() {
        Furnizor f = FurnizorFactory.creeaza(
            "RO12345678",
            "Furnizor Premium SRL",
            new Adresa("Str. Industriala 10", "Iasi", "700000", "Romania"),
            Set.of(new Email("contact@furnizor.ro")),
            new Iban("RO49AAAA1B31007593840000"),
            Set.of("MATERIE_PRIMA", "AMBALAJE"),
            new ConditiiContractuale(30, Currency.getInstance("RON"), Collections.emptyMap())
        );

        assertNotNull(f);
        assertEquals(StareFurnizor.Candidat, f.getStareFurnizor());
        assertDoesNotThrow(() -> calitateValidationService.valideazaFurnizor(f));
    }

    @Test
    @DisplayName("FurnizorFactory: Scenariu invalid - lipsa cod fiscal sau categorii")
    void testFurnizorFactory_InvalidScenarios() {
        assertThrows(DomainValidationException.class, () ->
            FurnizorFactory.creeaza(
                null,
                "Furnizor SRL",
                new Adresa("Str. 1", "Iasi", "700000", "Romania"),
                Collections.emptySet(),
                new Iban("RO49AAAA1B31007593840000"),
                Set.of("CAT-1"),
                new ConditiiContractuale(30, Currency.getInstance("RON"), Collections.emptyMap())
            )
        );

        assertThrows(DomainValidationException.class, () ->
            FurnizorFactory.creeaza(
                "RO1234",
                "Furnizor SRL",
                new Adresa("Str. 1", "Iasi", "700000", "Romania"),
                Collections.emptySet(),
                new Iban("RO49AAAA1B31007593840000"),
                Collections.emptySet(),
                new ConditiiContractuale(30, Currency.getInstance("RON"), Collections.emptyMap())
            )
        );
    }

    @Test
    @DisplayName("CalitateValidation: PlanAQL validation - esantion nu poate depasi lotul")
    void testCalitateValidation_PlanAQLEsantionDepasit() {
        PlanAQL plan = PlanAQL.standard(1.5, 100, 2);

        // Valid: Lot size 500 >= Sample 100
        assertDoesNotThrow(() -> calitateValidationService.valideazaPlanAQL(plan, 500));

        // Invalid: Lot size 50 < Sample 100
        DomainValidationException ex = assertThrows(DomainValidationException.class, () ->
            calitateValidationService.valideazaPlanAQL(plan, 50)
        );
        assertEquals("ESANTION_DEPASIT", ex.getCodEroare());
    }

    @Test
    @DisplayName("CalitateValidation: Inspectie programata in afara certificarii inspectorului")
    void testCalitateValidation_CertificareInspectorExpirata() {
        PlanAQL plan = PlanAQL.standard(1.0, 50, 1);
        Perioada perioadaInspectie = Perioada.of(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 5));

        InspectieCalitate ins = InspectieCalitateFactory.programeaza(
            IdLot.random(),
            IdComanda.random(),
            IdFurnizor.random(),
            CodMaterial.of("MAT-01"),
            plan,
            perioadaInspectie
        );

        // Inspector certificare expira inainte de inspectie (in mai 2026)
        Perioada certificareExpirata = Perioada.of(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 5, 20));

        DomainValidationException ex = assertThrows(DomainValidationException.class, () ->
            calitateValidationService.valideazaInspectieProgramata(ins, certificareExpirata)
        );
        assertEquals("CERTIFICARE_INSPECTOR_EXPIRATA", ex.getCodEroare());

        // Inspector cu certificare valida pe toata perioada
        Perioada certificareValida = Perioada.of(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));
        assertDoesNotThrow(() -> calitateValidationService.valideazaInspectieProgramata(ins, certificareValida));
    }

    // =========================================================================
    // 3. FacturaValidationService & Factura / NIR Factories Tests
    // =========================================================================

    @Test
    @DisplayName("FacturaFactory & NIRFactory: Scenarii valide de creare a agregatelor")
    void testFacturaSiNIRFactory_ValidScenarios() {
        IdComanda comandaId = IdComanda.random();
        IdFurnizor furnizorId = IdFurnizor.random();
        IdLot lotId = IdLot.random();

        LinieFactura lf = new LinieFactura(
            CodMaterial.of("MAT-01"),
            BigDecimal.valueOf(100),
            "BUC",
            new Suma(BigDecimal.valueOf(50), Currency.getInstance("RON")),
            BigDecimal.valueOf(0.19)
        );

        FacturaFurnizor factura = FacturaFactory.creeaza(
            "FC",
            "100234",
            LocalDate.now(),
            LocalDate.now().plusDays(30),
            comandaId,
            furnizorId,
            List.of(lf),
            Currency.getInstance("RON")
        );

        assertNotNull(factura);
        assertEquals(StareFactura.Inregistrata, factura.getStareFactura());
        assertDoesNotThrow(() -> facturaValidationService.valideazaFacturaInregistrare(factura));

        LinieNIR ln = new LinieNIR(lotId, CodMaterial.of("MAT-01"), BigDecimal.valueOf(100), "BUC");
        NIR nir = NIRFactory.creeaza("NIR-9901", comandaId, UUID.randomUUID(), LocalDate.now(), List.of(ln));
        assertNotNull(nir);
        assertEquals(StareNIR.Generat, nir.getStareNIR());
    }

    @Test
    @DisplayName("FacturaValidation: 3-Way Match in toleranta de 2% vs depasire toleranta")
    void testFacturaValidation_3WayMatchTolerance() {
        IdComanda comandaId = IdComanda.random();
        IdFurnizor furnizorId = IdFurnizor.random();
        IdLot lotId = IdLot.random();

        // Comanda: 100 BUC x 50 RON = 5000 RON fara TVA
        LinieSumar linieSumar = new LinieSumar(CodMaterial.of("MAT-01"), BigDecimal.valueOf(100), BigDecimal.valueOf(50));
        ComandaSummaryVO comanda = new ComandaSummaryVO(comandaId, List.of(linieSumar));

        LinieNIR ln = new LinieNIR(lotId, CodMaterial.of("MAT-01"), BigDecimal.valueOf(100), "BUC");
        NIR nir = NIRFactory.creeaza("NIR-01", comandaId, UUID.randomUUID(), LocalDate.now(), List.of(ln));

        // Factura cu diferenta de 1% (5050 RON fara TVA) -> Admisibila in toleranta de 2%
        LinieFactura lf1 = new LinieFactura(
            CodMaterial.of("MAT-01"),
            BigDecimal.valueOf(100),
            "BUC",
            new Suma(BigDecimal.valueOf(50.5), Currency.getInstance("RON")),
            BigDecimal.valueOf(0.19)
        );
        FacturaFurnizor f1 = FacturaFactory.creeaza("FC", "1", LocalDate.now(), LocalDate.now().plusDays(30), comandaId, furnizorId, List.of(lf1), Currency.getInstance("RON"));
        assertDoesNotThrow(() -> facturaValidationService.valideaza3WayMatch(f1, nir, comanda, 2.0));

        // Factura cu diferenta de 5% (5250 RON fara TVA) -> Depaseste toleranta de 2%
        LinieFactura lf2 = new LinieFactura(
            CodMaterial.of("MAT-01"),
            BigDecimal.valueOf(100),
            "BUC",
            new Suma(BigDecimal.valueOf(52.5), Currency.getInstance("RON")),
            BigDecimal.valueOf(0.19)
        );
        FacturaFurnizor f2 = FacturaFactory.creeaza("FC", "2", LocalDate.now(), LocalDate.now().plusDays(30), comandaId, furnizorId, List.of(lf2), Currency.getInstance("RON"));

        DomainValidationException ex = assertThrows(DomainValidationException.class, () ->
            facturaValidationService.valideaza3WayMatch(f2, nir, comanda, 2.0)
        );
        assertEquals("DISCREPANTA_3WAY_MATCH", ex.getCodEroare());
    }

    @Test
    @DisplayName("FacturaValidation: Ordin de plata depaseste restul de plata sau stare neaprobata")
    void testFacturaValidation_OrdinPlataRestrictions() {
        IdComanda comandaId = IdComanda.random();
        IdFurnizor furnizorId = IdFurnizor.random();

        LinieFactura lf = new LinieFactura(
            CodMaterial.of("MAT-01"),
            BigDecimal.valueOf(10),
            "BUC",
            new Suma(BigDecimal.valueOf(100), Currency.getInstance("RON")),
            BigDecimal.valueOf(0.19)
        );
        // Total factura: 1000 + 190 TVA = 1190 RON
        FacturaFurnizor f = FacturaFactory.creeaza("FC", "OP-TEST", LocalDate.now(), LocalDate.now().plusDays(30), comandaId, furnizorId, List.of(lf), Currency.getInstance("RON"));

        // Scenariu 1: Inregistrata (neaprobata) -> Nu poate primi ordin de plata
        DomainValidationException exStare = assertThrows(DomainValidationException.class, () ->
            facturaValidationService.valideazaOrdinPlata(f, BigDecimal.valueOf(500))
        );
        assertEquals("STARE_FACTURA_INELIGIBILA_PLATA", exStare.getCodEroare());

        // Trecere in InVerificare si Aprobare
        f.trimiteLaVerificare();
        f.aproba();

        // Scenariu 2: Suma depaseste restul de plata (1500 RON > 1190 RON)
        DomainValidationException exSuma = assertThrows(DomainValidationException.class, () ->
            facturaValidationService.valideazaOrdinPlata(f, BigDecimal.valueOf(1500))
        );
        assertEquals("SUMA_DEPASESTE_RESTUL", exSuma.getCodEroare());

        // Scenariu 3: Suma valida (500 RON <= 1190 RON)
        assertDoesNotThrow(() -> facturaValidationService.valideazaOrdinPlata(f, BigDecimal.valueOf(500)));
    }

    // =========================================================================
    // 4. Bean Validation (Jakarta Validation Constraints) Tests
    // =========================================================================

    @Test
    @DisplayName("Bean Validation: Entitati create valid respecta toate constrangerile de validare")
    void testBeanValidation_ValidEntities_NoViolations() {
        ComandaAprovizionare comanda = ComandaFactory.creeaza(
            "CMD-2026-BV-01",
            IdFurnizor.random(),
            UUID.randomUUID(),
            LocalDate.now(),
            LocalDate.now().plusDays(10),
            "Depozit Central",
            List.of(new LinieComanda(CodMaterial.of("MAT-01"), Cantitate.of(10, "BUC"), PretUnitar.of(15, "RON")))
        );
        assertTrue(validator.validate(comanda).isEmpty(), "Comanda valida nu trebuie sa aiba incalcari Bean Validation");

        Furnizor furnizor = FurnizorFactory.creeaza(
            "RO12345678",
            "Furnizor Test SRL",
            new Adresa("Str. Industriala 10", "Iasi", "700000", "Romania"),
            Set.of(new Email("contact@test.ro")),
            new Iban("RO49AAAA1B31007593840000"),
            Set.of("MateriiPrime"),
            new ConditiiContractuale(30, Currency.getInstance("RON"), Collections.emptyMap())
        );
        assertTrue(validator.validate(furnizor).isEmpty(), "Furnizor valid nu trebuie sa aiba incalcari Bean Validation");

        InspectieCalitate inspectie = InspectieCalitateFactory.programeaza(
            IdLot.random(),
            comanda.getId(),
            furnizor.getId(),
            CodMaterial.of("MAT-01"),
            PlanAQL.standard(1.5, 20, 1),
            Perioada.of(LocalDate.now(), LocalDate.now().plusDays(5))
        );
        assertTrue(validator.validate(inspectie).isEmpty(), "Inspectie valida nu trebuie sa aiba incalcari Bean Validation");

        FacturaFurnizor factura = FacturaFactory.creeaza(
            "FC",
            "9901",
            LocalDate.now(),
            LocalDate.now().plusDays(30),
            comanda.getId(),
            furnizor.getId(),
            List.of(new LinieFactura(CodMaterial.of("MAT-01"), BigDecimal.valueOf(10), "BUC", Suma.of(15, "RON"), BigDecimal.valueOf(0.19))),
            Currency.getInstance("RON")
        );
        assertTrue(validator.validate(factura).isEmpty(), "Factura valida nu trebuie sa aiba incalcari Bean Validation");

        NIR nir = NIRFactory.creeaza(
            "NIR-1234",
            comanda.getId(),
            UUID.randomUUID(),
            LocalDate.now(),
            List.of(new LinieNIR(IdLot.random(), CodMaterial.of("MAT-01"), BigDecimal.valueOf(10), "BUC"))
        );
        assertTrue(validator.validate(nir).isEmpty(), "NIR valid nu trebuie sa aiba incalcari Bean Validation");
    }

    @Test
    @DisplayName("Bean Validation: Constrangeri incalcate sunt detectate corespunzator")
    void testBeanValidation_InvalidConstraints_ViolationsDetected() {
        LinieFactura lfInvalid = new LinieFactura(
            CodMaterial.of("MAT-01"),
            BigDecimal.valueOf(-5), // cantitate negativa -> incalca @DecimalMin("0.01")
            "BUC",
            Suma.of(15, "RON"),
            BigDecimal.valueOf(1.5)  // TVA > 1.0 -> incalca @DecimalMax("1.00")
        );
        Set<ConstraintViolation<LinieFactura>> violations = validator.validate(lfInvalid);
        assertFalse(violations.isEmpty(), "LinieFactura invalida trebuie sa declanseze erori Bean Validation");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cantitate")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cotaTVA")));
    }

    // =========================================================================
    // 5. Business Policies Tests (Other Business Rules - ADR-003)
    // =========================================================================

    @Test
    @DisplayName("PoliticaAprobareComanda: Praguri valorice si ierarhie nivele de aprobare")
    void testPoliticaAprobareComanda_PraguriSiIerarhie() {
        // Praguri: < 10k (Responsabil), 10k - 50k (Manager), > 50k (Director General)
        assertEquals(PoliticaAprobareComanda.NivelAprobare.RESPONSABIL_ACHIZITII,
                politicaAprobareComanda.determinaNivelNecesar(BigDecimal.valueOf(5000)));
        assertEquals(PoliticaAprobareComanda.NivelAprobare.MANAGER_DEPARTAMENT,
                politicaAprobareComanda.determinaNivelNecesar(BigDecimal.valueOf(10000)));
        assertEquals(PoliticaAprobareComanda.NivelAprobare.MANAGER_DEPARTAMENT,
                politicaAprobareComanda.determinaNivelNecesar(BigDecimal.valueOf(45000)));
        assertEquals(PoliticaAprobareComanda.NivelAprobare.DIRECTOR_GENERAL,
                politicaAprobareComanda.determinaNivelNecesar(BigDecimal.valueOf(50000.01)));

        // Poate aproba in functie de rang
        assertTrue(politicaAprobareComanda.poateAproba(
                PoliticaAprobareComanda.NivelAprobare.MANAGER_DEPARTAMENT, BigDecimal.valueOf(8000)));
        assertTrue(politicaAprobareComanda.poateAproba(
                PoliticaAprobareComanda.NivelAprobare.DIRECTOR_GENERAL, BigDecimal.valueOf(75000)));
        assertFalse(politicaAprobareComanda.poateAproba(
                PoliticaAprobareComanda.NivelAprobare.RESPONSABIL_ACHIZITII, BigDecimal.valueOf(25000)));
    }

    @Test
    @DisplayName("PoliticaSelectieAQL: Selectie regim si generare plan ISO 2859-1")
    void testPoliticaSelectieAQL_RegimuriSiPlanuri() {
        assertEquals(PoliticaSelectieAQL.RegimInspectie.REDUS, politicaSelectieAQL.determinaRegim(Calificativ.A));
        assertEquals(PoliticaSelectieAQL.RegimInspectie.NORMAL, politicaSelectieAQL.determinaRegim(Calificativ.B));
        assertEquals(PoliticaSelectieAQL.RegimInspectie.SEVER, politicaSelectieAQL.determinaRegim(Calificativ.C));
        assertEquals(PoliticaSelectieAQL.RegimInspectie.TOTAL_100_PROCENT, politicaSelectieAQL.determinaRegim(Calificativ.D));

        // Verificare invariant ISO 2859-1 pe planurile generate (Re = Ac + 1)
        PlanAQL planA = politicaSelectieAQL.determinaPlan(Calificativ.A, 100);
        assertEquals(planA.getNumarAcceptare() + 1, planA.getNumarRespingere());

        PlanAQL planB = politicaSelectieAQL.determinaPlan(Calificativ.B, 100);
        assertEquals(planB.getNumarAcceptare() + 1, planB.getNumarRespingere());
        assertTrue(planB.getDimensiuneEsantion() >= planA.getDimensiuneEsantion());

        PlanAQL planD = politicaSelectieAQL.determinaPlan(Calificativ.D, 50);
        assertEquals(50, planD.getDimensiuneEsantion(), "Pentru furnizor critic (Calificativ D) esantionul este 100%");
        assertEquals(0, planD.getNumarAcceptare());
        assertEquals(1, planD.getNumarRespingere());
    }

    @Test
    @DisplayName("PoliticaAutorizarePlata: Four-Eyes Principle, stare factura si prag CFO")
    void testPoliticaAutorizarePlata_FourEyesSiPragCFO() {
        UUID creatorId = UUID.randomUUID();
        UUID aprobatorId = UUID.randomUUID();

        // 1. Four-Eyes Principle
        assertTrue(politicaAutorizarePlata.respectaFourEyesPrinciple(creatorId, aprobatorId));
        assertFalse(politicaAutorizarePlata.respectaFourEyesPrinciple(creatorId, creatorId));

        IllegalStateException exSame = assertThrows(IllegalStateException.class, () ->
            politicaAutorizarePlata.valideazaAutorizare(creatorId, creatorId, Suma.of(5000, "RON"), StareFactura.AprobataLaPlata, false)
        );
        assertTrue(exSame.getMessage().contains("Four-Eyes Principle incalcat"));

        // 2. Stare factura ineligibila
        IllegalStateException exStare = assertThrows(IllegalStateException.class, () ->
            politicaAutorizarePlata.valideazaAutorizare(creatorId, aprobatorId, Suma.of(5000, "RON"), StareFactura.Inregistrata, false)
        );
        assertTrue(exStare.getMessage().contains("Factura nu este eligibila pentru plata"));

        // 3. Prag CFO (> 20,000 RON fara rol CFO)
        IllegalStateException exCFO = assertThrows(IllegalStateException.class, () ->
            politicaAutorizarePlata.valideazaAutorizare(creatorId, aprobatorId, Suma.of(25000, "RON"), StareFactura.AprobataLaPlata, false)
        );
        assertTrue(exCFO.getMessage().contains("Directorului Financiar (CFO)"));

        // 4. Autorizare cu succes (suma mare cu CFO)
        assertDoesNotThrow(() ->
            politicaAutorizarePlata.valideazaAutorizare(creatorId, aprobatorId, Suma.of(25000, "RON"), StareFactura.AprobataLaPlata, true)
        );

        // 5. Autorizare cu succes (suma mica sub prag)
        assertDoesNotThrow(() ->
            politicaAutorizarePlata.valideazaAutorizare(creatorId, aprobatorId, Suma.of(15000, "RON"), StareFactura.AprobataLaPlata, false)
        );
    }
}
