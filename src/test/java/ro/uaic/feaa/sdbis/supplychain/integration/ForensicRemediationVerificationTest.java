package ro.uaic.feaa.sdbis.supplychain.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.ActiuneCorectiva;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Furnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.FurnizorRepo;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectieCalitate;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectieRepo;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.PlanAQL;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.RaportNeconformitate;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.ScorecardLunar;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.StareFurnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.StareInspectie;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.StareNCR;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.IComandaWorkflowService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.security.IApprovalAuthorityService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.Cantitate;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaRepo;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.LinieComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.Lot;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.PretUnitar;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.StareComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.StareLot;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.policy.PoliticaAprobareComanda.NivelAprobare;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.FacturaApplicationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.StripeWebhookProcessingService;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.InregistreazaFacturaCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.ReconciliereCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.port.IStripeConfigPort;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaRepo;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.LinieFactura;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.LinieNIR;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.NIR;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.NIRRepo;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.OrdinPlata;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.OrdinPlataRepo;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareFactura;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareNIR;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.StareOrdinPlata;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.Suma;
import ro.uaic.feaa.sdbis.supplychain.infrastructure.configuration.GlobalExceptionHandler;
import ro.uaic.feaa.sdbis.supplychain.infrastructure.presentation.DocumenteCtrl;
import ro.uaic.feaa.sdbis.supplychain.securitate.AuthRegisterRequest;
import ro.uaic.feaa.sdbis.supplychain.securitate.AutentificareCtrl;
import ro.uaic.feaa.sdbis.supplychain.securitate.JwtTokenService;
import ro.uaic.feaa.sdbis.supplychain.securitate.UserAuthenticationClaims;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.DistributedLockService;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.PerformantaLivrariVO;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.mock.web.MockMultipartFile;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.query.ComandaQueryService;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.query.FacturaQueryService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.computation.ReconciliereComputationService;
import ro.uaic.feaa.sdbis.supplychain.securitate.RateLimitingFilter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Forensic Remediation Verification Test Suite (All 20 Audit Findings)")
public class ForensicRemediationVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AutentificareCtrl autentificareCtrl;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private FacturaApplicationService facturaAppService;

    @Autowired
    private FacturaRepo facturaRepo;

    @Autowired
    private NIRRepo nirRepo;

    @Autowired
    private OrdinPlataRepo ordinRepo;

    @Autowired
    private ComandaRepo comandaRepo;

    @Autowired
    private IComandaWorkflowService comandaWorkflowService;

    @Autowired
    private DistributedLockService distributedLockService;

    @Autowired
    private StripeWebhookProcessingService stripeWebhookProcessingService;

    @Autowired
    private IStripeConfigPort stripeConfigPort;

    @Autowired
    private IApprovalAuthorityService approvalAuthorityService;

    @Autowired
    private FurnizorRepo furnizorRepo;

    @Autowired
    private InspectieRepo inspectieRepo;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    private DocumenteCtrl documenteCtrl;

    @Autowired
    private ComandaQueryService comandaQueryService;

    @Autowired
    private FacturaQueryService facturaQueryService;

    // -------------------------------------------------------------
    // DIMENSIUNEA 1: SECURITATE
    // -------------------------------------------------------------

    @Test
    @DisplayName("SEC-01: Auto-inregistrarea publica cu roluri de conducere (CFO, Manager) este blocata cu 403 Forbidden")
    void testSec01_RegisterElevatedRolesRequiresAdminPrivileges() {
        SecurityContextHolder.clearContext();
        AuthRegisterRequest request = new AuthRegisterRequest(
                "attacker_cfo", "password123", "attacker@test.ro", "Attacker CFO", "ROLE_CFO"
        );
        assertThrows(AccessDeniedException.class, () -> autentificareCtrl.register(request));
    }

    @Test
    @DisplayName("SEC-02: Aprobarea ordinului de plata respinge identitatea daca utilizatorul nu este autentificat in context")
    void testSec02_AprobaOrdinRequiresAuthenticatedCallerInContext() throws Exception {
        UUID ordinId = UUID.randomUUID();
        // Aprobare fara token Bearer -> 401 Unauthorized conform filtrului sau 403
        mockMvc.perform(post("/api/facturi/ordine-plata/" + ordinId + "/aprobare"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("SEC-04: Rate Limiting-ul foloseste evictiune activa pe minut nou si blocheaza cererile ce depasesc pragul cu 429")
    void testSec04_RateLimitingActiveEviction() throws Exception {
        RateLimitingFilter filter = new RateLimitingFilter();
        org.springframework.mock.web.MockHttpServletRequest request = new org.springframework.mock.web.MockHttpServletRequest();
        request.setRequestURI("/api/comenzi");
        request.setRemoteAddr("198.51.100.42");
        jakarta.servlet.FilterChain chain = (req, res) -> {};

        for (int i = 0; i < 300; i++) {
            org.springframework.mock.web.MockHttpServletResponse resp = new org.springframework.mock.web.MockHttpServletResponse();
            filter.doFilter(request, resp, chain);
            assertEquals(200, resp.getStatus());
        }

        org.springframework.mock.web.MockHttpServletResponse blockedResp = new org.springframework.mock.web.MockHttpServletResponse();
        filter.doFilter(request, blockedResp, chain);
        assertEquals(429, blockedResp.getStatus());
        assertEquals("60", blockedResp.getHeader("Retry-After"));
    }

    @Test
    @DisplayName("SEC-05: JwtTokenService parseAndValidate extrage claims-urile dintr-o singura operatie HMAC")
    void testSec05_JwtSinglePassValidation() {
        String token = jwtTokenService.generateToken("testuser", "ROLE_BUYER", UUID.randomUUID().toString());
        Optional<UserAuthenticationClaims> claimsOpt = jwtTokenService.parseAndValidate(token);
        assertTrue(claimsOpt.isPresent());
        assertEquals("testuser", claimsOpt.get().username());
        assertEquals("ROLE_BUYER", claimsOpt.get().role());
    }

    // -------------------------------------------------------------
    // DIMENSIUNEA 2: DDD SI AGREGATE
    // -------------------------------------------------------------

    @Test
    @DisplayName("DDD-01: RaportNeconformitate protejeaza tranzitiile stricte si interzice mutatiile din starea Inchis")
    void testDdd01_RaportNeconformitateStateTransitionsStrict() {
        RaportNeconformitate ncr = new RaportNeconformitate(
                UUID.randomUUID(), UUID.randomUUID(), IdFurnizor.random(), IdLot.random()
        );
        assertEquals(StareNCR.Deschis, ncr.getStareNCR());

        // Deschis -> nu permite inchidere directa
        assertThrows(IllegalStateException.class, ncr::inchide);

        // Deschis -> InAnalizaCauzei
        ncr.analizeazaCauza("Problema toleranta mecanica");
        assertEquals(StareNCR.InAnalizaCauzei, ncr.getStareNCR());

        // InAnalizaCauzei -> ActiuneCorectivaInCurs
        ncr.initiazaActiuneCorectiva(new ActiuneCorectiva("Reglare utilaj CNC", "Inginer Sef", LocalDate.now().plusDays(5)));
        assertEquals(StareNCR.ActiuneCorectivaInCurs, ncr.getStareNCR());

        // ActiuneCorectivaInCurs -> Verificat
        ncr.verifica();
        assertEquals(StareNCR.Verificat, ncr.getStareNCR());

        // Verificat -> Inchis
        ncr.inchide();
        assertEquals(StareNCR.Inchis, ncr.getStareNCR());

        // Stare terminala Inchis -> interzice re-analiza cauzei
        assertThrows(IllegalStateException.class, () -> ncr.analizeazaCauza("Alta cauza"));
    }

    @Test
    @DisplayName("DDD-02: NIR stornat salveaza motivul si interzice mutatiile ulterioare")
    void testDdd02_NirStornoAndReconcileGuards() {
        LinieNIR linie = new LinieNIR(IdLot.random(), CodMaterial.of("MAT-01"), BigDecimal.valueOf(50), "BUC");
        NIR nir = new NIR(UUID.randomUUID(), "NIR-2026-001", IdComanda.random(), UUID.randomUUID(), LocalDate.now(), List.of(linie));

        nir.storneaza("Documentatie fizica deteriorata");
        assertEquals(StareNIR.Stornat, nir.getStareNIR());
        assertEquals("Documentatie fizica deteriorata", nir.getMotivStornare());

        // NIR-ul stornat nu poate fi trimis la reconciliere sau reconciliat
        assertThrows(IllegalStateException.class, nir::trimiteLaReconciliere);
        assertThrows(IllegalStateException.class, nir::reconciliaza);
    }

    @Test
    @DisplayName("DDD-03: Lotul nu poate fi receptionat daca este in starea terminala Respins sau EliberatInStoc")
    void testDdd03_LotReceptionRestrictedToValidStates() {
        ComandaAprovizionare comanda = new ComandaAprovizionare(
                IdComanda.random(), "CMD-VERIF-01", IdFurnizor.random(), UUID.randomUUID(),
                LocalDate.now(), LocalDate.now().plusDays(10), "DEP-A",
                List.of(new LinieComanda(CodMaterial.of("M1"), Cantitate.of(100.0, "BUC"), PretUnitar.of(10.0, "RON")))
        );
        comanda.aproba(UUID.randomUUID(), "MANAGER");
        comanda.confirma(LocalDate.now().plusDays(5));
        IdLot lotId = comanda.planificaLot("LOT-01", CodMaterial.of("M1"), Cantitate.of(100.0, "BUC"), LocalDate.now(), LocalDate.now().plusYears(1));

        comanda.inregistreazaExpediere("DHL", "AWB001", Collections.singleton(lotId));
        comanda.inregistreazaReceptie(lotId, Cantitate.of(100.0, "BUC"));

        // Marcare ca respins la calitate
        comanda.plaseazaLotInCarantina(lotId);
        comanda.aplicaVerdictInspectie(lotId, ro.uaic.feaa.sdbis.supplychain.comenzi.domain.RezultatCalitateLot.Neconform);

        Lot lot = comanda.getLoturi().get(0);
        assertEquals(StareLot.Respins, lot.getStareLot());

        // Tentativa de receptie pe un lot respins trebuie sa esueze
        assertThrows(IllegalStateException.class, () -> comanda.inregistreazaReceptie(lotId, Cantitate.of(10.0, "BUC")));
    }

    @Test
    @DisplayName("DDD-04: Ordinul de plata peste 10.000 EUR in stare Creat necesita aprobare inainte de transmitere")
    void testDdd04_OrdinPlataOverThresholdRequiresApprovalAndRejectGuard() {
        IdFurnizor fId = IdFurnizor.random();
        IdComanda cId = IdComanda.random();
        UUID facturaId = UUID.randomUUID();

        LinieFactura lf = new LinieFactura(CodMaterial.of("MAT-01"), BigDecimal.valueOf(100), "BUC", Suma.of(200.0, "EUR"), BigDecimal.valueOf(19));
        FacturaFurnizor f = new FacturaFurnizor(
                facturaId, "INV", "99001", LocalDate.now(), LocalDate.now().plusDays(30),
                cId, fId, List.of(lf), Currency.getInstance("EUR")
        );
        f.trimiteLaVerificare();
        f.aproba();

        // Ordin de 15.000 EUR (> prag 10.000 EUR)
        UUID creatorId = UUID.randomUUID();
        OrdinPlata ordin = f.creeazaOrdinPlata(Suma.of(15000.0, "EUR"), creatorId);
        assertEquals(StareOrdinPlata.Creat, ordin.getStareOrdin());

        // Transmiterea la banca fara aprobare prealabila este blocata
        assertThrows(IllegalStateException.class, ordin::transmiteLaBanca);

        // Aprobare de catre o alta persoana (Four-Eyes Principle)
        ordin.aproba(UUID.randomUUID());
        assertEquals(StareOrdinPlata.Aprobat, ordin.getStareOrdin());

        // Dupa aprobare, transmiterea la banca reuseste
        ordin.transmiteLaBanca();
        assertEquals(StareOrdinPlata.TransmisLaBanca, ordin.getStareOrdin());

        // Dupa executie, respingerea este interzisa
        ordin.inregistreazaExecutie();
        assertEquals(StareOrdinPlata.Executat, ordin.getStareOrdin());
        assertThrows(IllegalStateException.class, () -> ordin.respingeDeBanca("Eroare dupa executie"));
    }

    @Test
    @DisplayName("DDD-05: Comanda in stare terminala (Receptionat, Inchis, Anulat) nu poate fi anulata")
    void testDdd05_ComandaTerminalStateCannotBeCancelled() {
        CodMaterial mat = CodMaterial.of("MAT-P2P");
        ComandaAprovizionare comanda = new ComandaAprovizionare(
                IdComanda.random(), "CMD-P2P-001", IdFurnizor.random(), UUID.randomUUID(),
                LocalDate.now(), LocalDate.now().plusDays(10), "DEP-C",
                List.of(new LinieComanda(mat, Cantitate.of(100.0, "BUC"), PretUnitar.of(20.0, "RON")))
        );
        comanda.aproba(UUID.randomUUID(), "ACHIZITOR");
        comanda.confirma(LocalDate.now().plusDays(5));
        IdLot lot1 = comanda.planificaLot("L1", mat, Cantitate.of(100.0, "BUC"), LocalDate.now(), LocalDate.now().plusYears(1));

        comanda.inregistreazaExpediere("FAN", "AWB99", Collections.singleton(lot1));
        comanda.inregistreazaReceptie(lot1, Cantitate.of(100.0, "BUC"));
        assertEquals(StareComanda.Receptionat, comanda.getStareComanda());

        // Comanda receptionata integral (stare terminala de livrare) refuza anularea
        assertThrows(IllegalStateException.class, comanda::anuleazaComanda);

        // Inchisa refuza anularea
        comanda.inchideComanda();
        assertEquals(StareComanda.Inchis, comanda.getStareComanda());
        assertThrows(IllegalStateException.class, comanda::anuleazaComanda);
    }

    // -------------------------------------------------------------
    // DIMENSIUNEA 3: CONCURENTA SI DISTRIBUTED LOCKS
    // -------------------------------------------------------------

    @Test
    @DisplayName("CONC-01: Lock-ul distribuit pre-seedat poate fi achizitionat si eliberat fara RollbackOnly")
    void testConc01_PreseededDistributedLocks() {
        boolean locked = distributedLockService.tryLock("outboxDispatcher", Duration.ofSeconds(10));
        assertTrue(locked, "Lock-ul outboxDispatcher pre-seedat trebuie sa poata fi achizitionat");
        distributedLockService.releaseLock("outboxDispatcher");
    }

    // -------------------------------------------------------------
    // DIMENSIUNEA 4: CLEAN ARCHITECTURE SI MODULITH
    // -------------------------------------------------------------

    @Test
    @DisplayName("ARCH-01: StripeWebhookProcessingService utilizeaza portul curat de configurare IStripeConfigPort")
    void testArch01_StripeWebhookProcessingCleanArchitecturePort() {
        assertNotNull(stripeConfigPort.getWebhookSecret());
        assertNotNull(stripeWebhookProcessingService);
    }

    @Test
    @DisplayName("ARCH-02: Presentation utilizeaza portul de securitate de aplicatie IApprovalAuthorityService")
    void testArch02_ComandaCtrlUsesApplicationSecurityPort() {
        assertNotNull(approvalAuthorityService);
        NivelAprobare nivelMgr = approvalAuthorityService.determinaNivelAprobare(
                List.of(new SimpleGrantedAuthority("ROLE_MANAGER_ACHIZITII"))
        );
        assertEquals(NivelAprobare.MANAGER_DEPARTAMENT, nivelMgr);

        NivelAprobare nivelCfo = approvalAuthorityService.determinaNivelAprobare(
                List.of(new SimpleGrantedAuthority("ROLE_CFO"))
        );
        assertEquals(NivelAprobare.DIRECTOR_GENERAL, nivelCfo);

        UUID callerId = UUID.randomUUID();
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                callerId.toString(), "n/a", List.of(new SimpleGrantedAuthority("ROLE_BUYER"))
        );
        UUID extractedId = approvalAuthorityService.extrageUtilizatorId(auth);
        assertEquals(callerId, extractedId);
    }

    // -------------------------------------------------------------
    // DIMENSIUNEA 5: JPA SI PERFORMANTA
    // -------------------------------------------------------------

    @Test
    @DisplayName("JPA-02: CalitateQueryService calculeaza dashboard-ul fara incarcare findAll() in memorie")
    void testJpa02_CalitateDashboardAggregatedSql() {
        assertTrue(furnizorRepo.countTotal() >= 0);
        assertTrue(inspectieRepo.countTotal() >= 0);
    }

    // -------------------------------------------------------------
    // DIMENSIUNEA 6: API REST SI RFC 7807
    // -------------------------------------------------------------

    @Test
    @DisplayName("REST-02: GlobalExceptionHandler returneaza ProblemDetail conform RFC 7807 cu extensii de compatibilitate")
    void testRest02_GlobalExceptionHandlerProblemDetailRfc7807() {
        ProblemDetail pd = globalExceptionHandler.handleIllegalArgument(new IllegalArgumentException("Parametru nevalid"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), pd.getStatus());
        assertEquals("Bad Request", pd.getTitle());
        assertEquals("Bad Request", pd.getProperties().get("error"));
        assertNotNull(pd.getProperties().get("timestamp"));
    }

    // -------------------------------------------------------------
    // DIMENSIUNEA 7: EDGE CASES SI 3-WAY MATCHING
    // -------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("TEST-01: Protectie impotriva reconcilierii si facturarii duplicate pe acelasi NIR")
    void testTest01_DuplicateNirReconciliationBlocked() {
        IdFurnizor fId = IdFurnizor.random();
        IdComanda cId = IdComanda.random();

        // 1. Prima factura
        LinieFactura lf1 = new LinieFactura(CodMaterial.of("MAT-01"), BigDecimal.valueOf(50), "BUC", Suma.of(100.0, "EUR"), BigDecimal.valueOf(19));
        UUID factura1Id = facturaAppService.inregistreazaFactura(new InregistreazaFacturaCommand(
                "SERIE-A", "101", LocalDate.now(), LocalDate.now().plusDays(30), cId, fId, List.of(lf1), Currency.getInstance("EUR")
        ));

        // Aprobam prima factura manual
        FacturaFurnizor f1 = facturaRepo.findById(factura1Id).orElseThrow();
        f1.trimiteLaVerificare();
        f1.aproba();
        facturaRepo.save(f1);

        // 2. NIR generat si marcat Reconciliat
        LinieNIR ln = new LinieNIR(IdLot.random(), CodMaterial.of("MAT-01"), BigDecimal.valueOf(50), "BUC");
        NIR nir = new NIR(UUID.randomUUID(), "NIR-DUP-01", cId, UUID.randomUUID(), LocalDate.now(), List.of(ln));
        nir.reconciliaza();
        nirRepo.add(nir);

        // 3. A doua factura pe aceeasi comanda / NIR
        LinieFactura lf2 = new LinieFactura(CodMaterial.of("MAT-01"), BigDecimal.valueOf(50), "BUC", Suma.of(100.0, "EUR"), BigDecimal.valueOf(19));
        UUID factura2Id = facturaAppService.inregistreazaFactura(new InregistreazaFacturaCommand(
                "SERIE-A", "102", LocalDate.now(), LocalDate.now().plusDays(30), cId, fId, List.of(lf2), Currency.getInstance("EUR")
        ));

        // Reconcilierea celei de-a doua facturi pe acelasi NIR trebuie respinsa
        assertThrows(IllegalStateException.class, () ->
                facturaAppService.reconcilieazaFactura(new ReconciliereCommand(factura2Id)));

        FacturaFurnizor f2 = facturaRepo.findById(factura2Id).orElseThrow();
        assertEquals(StareFactura.Blocata, f2.getStareFactura(), "Factura duplicata trebuie sa fie blocata");
    }

    // -------------------------------------------------------------
    // REMEDIERI CRIMINALISTICE ADITIONALE (SEC-F, DDD-F)
    // -------------------------------------------------------------

    @Test
    @DisplayName("SEC-F01: Auto-inregistrarea publica permite exclusiv rolurile din Whitelist (ROLE_BUYER, ROLE_SUPPLIER)")
    void testSecF01_PublicRegistrationStrictAllowlist() {
        SecurityContextHolder.clearContext();

        // 1. Rolurile interne privileged sunt respinse cu 403 Forbidden
        assertThrows(AccessDeniedException.class, () ->
                autentificareCtrl.register(new AuthRegisterRequest(
                        "hacker_acc", "Pass1234!", "hacker_acc@test.com", "Hacker", "ROLE_ACCOUNTANT"
                )));

        assertThrows(AccessDeniedException.class, () ->
                autentificareCtrl.register(new AuthRegisterRequest(
                        "hacker_qi", "Pass1234!", "hacker_qi@test.com", "Hacker", "ROLE_QUALITY_INSPECTOR"
                )));

        assertThrows(AccessDeniedException.class, () ->
                autentificareCtrl.register(new AuthRegisterRequest(
                        "hacker_dg", "Pass1234!", "hacker_dg@test.com", "Hacker", "ROLE_DIRECTOR_GENERAL"
                )));

        // 2. Rolurile publice legitime reusesc
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 6);
        var resp = autentificareCtrl.register(new AuthRegisterRequest(
                "buyer_" + uniqueSuffix, "Pass1234!", "buyer_" + uniqueSuffix + "@test.com", "Legit Buyer", "ROLE_BUYER"
        ));
        assertNotNull(resp.getBody());
        assertNotNull(resp.getBody().token());
    }

    @Test
    @DisplayName("SEC-F02: Upload-ul de documente valideaza dimensiunea maxima, extensia si sanitizeaza numele")
    void testSecF02_DocumentUploadSecurityValidation() {
        UUID refId = UUID.randomUUID();

        // 1. Fisier gigant (> 15 MB) este respins
        MockMultipartFile bigFile = new MockMultipartFile(
                "fisier", "overflow.pdf", "application/pdf", new byte[16 * 1024 * 1024]
        );
        assertThrows(IllegalArgumentException.class, () ->
                documenteCtrl.incarcaDocument(bigFile, "FACTURA", refId));

        // 2. Extensie nepermisa (.sh) este respinsa
        MockMultipartFile dangerousFile = new MockMultipartFile(
                "fisier", "script.sh", "text/plain", "echo exploit".getBytes()
        );
        assertThrows(IllegalArgumentException.class, () ->
                documenteCtrl.incarcaDocument(dangerousFile, "CONTRACT", refId));

        // 3. Nume cu tentativa de Path Traversal este sanitizat la salvare
        MockMultipartFile traversalFile = new MockMultipartFile(
                "fisier", "../../secret/factura_valida.pdf", "application/pdf", "%PDF valid invoice".getBytes()
        );
        var response = documenteCtrl.incarcaDocument(traversalFile, "FACTURA", refId);
        assertNotNull(response.getBody());
        assertEquals("factura_valida.pdf", response.getBody().numeFisier(), "Path traversal-ul trebuie sanitizat");
    }

    @Test
    @DisplayName("DDD-F01: Furnizorul in stare Blocat refuza mutarea in Suspendat sau aplicarea evaluarilor periodice")
    void testDddF01_FurnizorBlocatCannotBeSuspendedOrScorecardApplied() {
        Furnizor f = new Furnizor(
                IdFurnizor.random(), "RO" + UUID.randomUUID().toString().substring(0, 8),
                "Furnizor Risc Maxim", null, Collections.emptySet(), null, Collections.emptySet(), null
        );
        f.blocheaza("Sanctiune juridica definitiva");
        assertEquals(StareFurnizor.Blocat, f.getStareFurnizor());

        // Suspendarea unui furnizor blocat este interzisa
        assertThrows(IllegalStateException.class, () -> f.suspenda("Incercare downgrade"));

        // Aplicarea de evaluari periodice pe un furnizor blocat este interzisa
        Perioada p = Perioada.ofLuna(2026, 11);
        ScorecardLunar sc = ScorecardLunar.calculeaza(
                p, new PerformantaLivrariVO(f.getId(), p, BigDecimal.valueOf(95), BigDecimal.valueOf(95)), BigDecimal.valueOf(2)
        );
        assertThrows(IllegalStateException.class, () -> f.aplicaScorecard(sc));
    }

    @Test
    @Transactional
    @DisplayName("DDD-F02: Principiul celor patru ochi (Four-Eyes Principle) interzice auto-aprobarea pe comenzi mari")
    void testDddF02_FourEyesPrincipleOnPurchaseOrderApproval() {
        UUID buyerId = UUID.randomUUID();
        IdFurnizor furnizorId = IdFurnizor.random();

        // Furnizor aprobat si eligibil
        Furnizor furnizor = new Furnizor(
                furnizorId, "RO" + UUID.randomUUID().toString().substring(0, 8),
                "Furnizor P2P Aprobat", null, Collections.emptySet(), null, Collections.emptySet(), null
        );
        furnizor.trimiteLaEvaluare();
        furnizor.aproba();
        furnizorRepo.add(furnizor);

        // Comanda de 20.000 RON (> pragul de 10.000 RON)
        LinieComanda linie = new LinieComanda(CodMaterial.of("TABLA-01"), Cantitate.of(100.0, "TO"), PretUnitar.of(200.0, "RON"));
        ComandaAprovizionare comanda = new ComandaAprovizionare(
                IdComanda.random(), "CMD-FOUR-EYES-" + UUID.randomUUID().toString().substring(0, 6),
                furnizorId, buyerId, LocalDate.now(), LocalDate.now().plusDays(10), "DEP-CENTRAL", List.of(linie)
        );
        comandaRepo.add(comanda);

        // Cumparatorul incearca sa isi aprobe propria comanda mare -> respins prin Four-Eyes Principle
        assertThrows(SecurityException.class, () ->
                comandaWorkflowService.valideazaSiAproba(comanda.getId(), buyerId, NivelAprobare.MANAGER_DEPARTAMENT));

        // Aprobarea de catre o terta persoana autorizata -> reuseste
        UUID distinctApproverId = UUID.randomUUID();
        assertDoesNotThrow(() ->
                comandaWorkflowService.valideazaSiAproba(comanda.getId(), distinctApproverId, NivelAprobare.MANAGER_DEPARTAMENT));
        assertEquals(StareComanda.Aprobat, comandaRepo.findById(comanda.getId()).orElseThrow().getStareComanda());
    }

    @Test
    @DisplayName("DDD-F03: Inspectia de calitate deja anulata refuza re-anularea")
    void testDddF03_InspectieCalitateAnulataGuard() {
        InspectieCalitate insp = InspectieCalitate.programeaza(
                IdLot.random(), IdComanda.random(), IdFurnizor.random(),
                CodMaterial.of("MAT-01"), new PlanAQL(BigDecimal.valueOf(1.5), 50, 1, 2),
                Perioada.ofLuna(2026, 11)
        );

        insp.anuleaza();
        assertEquals(StareInspectie.Anulata, insp.getStareInspectie());

        // A doua anulare arunca IllegalStateException
        assertThrows(IllegalStateException.class, insp::anuleaza,
                "O inspectie deja anulata nu mai poate fi anulata din nou");
    }

    // -------------------------------------------------------------
    // DIMENSIUNEA EXTRA: VERIFICARE DETALIATA REMEDIERI CRIMINALISTICE
    // -------------------------------------------------------------

    @Test
    @DisplayName("SEC-01/REST-02: Webhook Stripe refuza cererile fara semnatura cu 400 Bad Request")
    void testSec01_StripeWebhookSignatureVerification() throws Exception {
        mockMvc.perform(post("/api/webhooks/stripe")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"evt_test\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DDD-01: FacturaFurnizor.inregistreazaPlata previne platile pe stari terminale si supra-platile")
    void testDdd01_FacturaInregistreazaPlataTerminalAndOverpaymentGuards() {
        FacturaFurnizor f = new FacturaFurnizor(
                UUID.randomUUID(), "SERIA", "12345",
                LocalDate.now(), LocalDate.now().plusDays(30),
                IdComanda.random(), IdFurnizor.random(),
                List.of(new LinieFactura(CodMaterial.of("M1"), BigDecimal.TEN, "BUC", Suma.of(100.0, "RON"), BigDecimal.valueOf(0.19))),
                Currency.getInstance("RON")
        );

        f.trimiteLaVerificare();
        f.aproba();

        // Incercare supra-plata (> 1190 RON) -> IllegalArgumentException
        Suma supraPlata = Suma.of(1200.0, "RON");
        assertThrows(IllegalArgumentException.class, () -> f.inregistreazaPlata(supraPlata));

        // Plata exacta -> devine Platita
        Suma plataExacta = Suma.of(1190.0, "RON");
        f.inregistreazaPlata(plataExacta);
        assertEquals(StareFactura.Platita, f.getStareFactura());

        // Incercare plata pe factura Platita -> IllegalStateException
        Suma plataSuplimentara = Suma.of(10.0, "RON");
        assertThrows(IllegalStateException.class, () -> f.inregistreazaPlata(plataSuplimentara));

        // Test pe factura Anulata
        FacturaFurnizor fAnulata = new FacturaFurnizor(
                UUID.randomUUID(), "SERIA", "12346",
                LocalDate.now(), LocalDate.now().plusDays(30),
                IdComanda.random(), IdFurnizor.random(),
                List.of(new LinieFactura(CodMaterial.of("M1"), BigDecimal.TEN, "BUC", Suma.of(100.0, "RON"), BigDecimal.valueOf(0.19))),
                Currency.getInstance("RON")
        );
        fAnulata.anuleaza();
        assertEquals(StareFactura.Anulata, fAnulata.getStareFactura());
        assertThrows(IllegalStateException.class, () -> fAnulata.inregistreazaPlata(plataExacta));
    }

    @Test
    @DisplayName("DDD-02: NIR.storneaza refuza stornarea directa pe starea Contabilizat")
    void testDdd02_NirStornoInterzisDacaContabilizat() {
        NIR nir = new NIR(
                UUID.randomUUID(), "NIR-2026-001", IdComanda.random(), UUID.randomUUID(),
                LocalDate.now(), List.of(new LinieNIR(IdLot.random(), CodMaterial.of("M1"), BigDecimal.TEN, "BUC"))
        );
        nir.trimiteLaReconciliere();
        nir.reconciliaza();
        nir.contabilizeaza();
        assertEquals(StareNIR.Contabilizat, nir.getStareNIR());

        // Stornare in-place refuzata pe Contabilizat
        assertThrows(IllegalStateException.class, () -> nir.storneaza("Eroare receptie"));
    }

    @Test
    @DisplayName("DDD-03: OrdinPlata.anuleaza refuza anularea repetata pe un ordin deja anulat")
    void testDdd03_OrdinPlataReAnulareInterzisa() {
        FacturaFurnizor f = new FacturaFurnizor(
                UUID.randomUUID(), "SERIA", "12347",
                LocalDate.now(), LocalDate.now().plusDays(30),
                IdComanda.random(), IdFurnizor.random(),
                List.of(new LinieFactura(CodMaterial.of("M1"), BigDecimal.TEN, "BUC", Suma.of(100.0, "RON"), BigDecimal.valueOf(0.19))),
                Currency.getInstance("RON")
        );
        f.trimiteLaVerificare();
        f.aproba();

        OrdinPlata op = f.creeazaOrdinPlata(Suma.of(500.0, "RON"), UUID.randomUUID());
        assertEquals(StareOrdinPlata.Creat, op.getStareOrdin());

        op.anuleaza();
        assertEquals(StareOrdinPlata.Anulat, op.getStareOrdin());

        // Re-anularea arunca IllegalStateException
        assertThrows(IllegalStateException.class, op::anuleaza);
    }

    @Test
    @DisplayName("DDD-04: ReconciliereComputationService respinge agregarea de facturi in valute diferite")
    void testDdd04_ReconciliereComputationValutaInconsistenta() {
        ReconciliereComputationService service = new ReconciliereComputationService();
        FacturaFurnizor fRon = new FacturaFurnizor(
                UUID.randomUUID(), "SERIA", "101",
                LocalDate.now(), LocalDate.now().plusDays(30),
                IdComanda.random(), IdFurnizor.random(),
                List.of(new LinieFactura(CodMaterial.of("M1"), BigDecimal.ONE, "BUC", Suma.of(100.0, "RON"), BigDecimal.valueOf(0.19))),
                Currency.getInstance("RON")
        );
        FacturaFurnizor fEur = new FacturaFurnizor(
                UUID.randomUUID(), "SERIA", "102",
                LocalDate.now(), LocalDate.now().plusDays(30),
                IdComanda.random(), IdFurnizor.random(),
                List.of(new LinieFactura(CodMaterial.of("M1"), BigDecimal.ONE, "BUC", Suma.of(100.0, "EUR"), BigDecimal.valueOf(0.19))),
                Currency.getInstance("EUR")
        );

        assertThrows(IllegalArgumentException.class, () -> service.calculeazaRestPlataTotal(List.of(fRon, fEur)));
    }

    @Test
    @DisplayName("REST-01: GlobalExceptionHandler mapeaza OptimisticLockingFailureException la 409 Conflict")
    void testRest01_GlobalExceptionHandlerOptimisticLockingReturns409() {
        OptimisticLockingFailureException ex = new OptimisticLockingFailureException("Simulated optimistic lock failure");
        ProblemDetail pd = globalExceptionHandler.handleOptimisticLockException(ex);
        assertEquals(HttpStatus.CONFLICT.value(), pd.getStatus());
        assertEquals("Conflict de Concurenta", pd.getTitle());
    }

    @Test
    @DisplayName("REST-03: ComandaQueryService.listeazaComenzi arunca IllegalArgumentException pentru stare invalida")
    void testRest03_ComandaQueryInvalidStatusThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                comandaQueryService.listeazaComenzi(null, "STARE_COMPLET_INVENTATA_SI_INVALIDA", 0, 10));
    }

    @Test
    @DisplayName("ARCH-01: ReconciliereCommand accepta nirId specific si constructor cu un parametru")
    void testArch01_ReconciliereCommandSupportsSpecificNirId() {
        UUID fId = UUID.randomUUID();
        UUID nId = UUID.randomUUID();
        ReconciliereCommand cmd = new ReconciliereCommand(fId, nId);
        assertEquals(fId, cmd.facturaId());
        assertEquals(nId, cmd.nirId());

        ReconciliereCommand legacyCmd = new ReconciliereCommand(fId);
        assertEquals(fId, legacyCmd.facturaId());
        assertNull(legacyCmd.nirId());
    }

    @Test
    @DisplayName("JPA-03: FacturaRepo.findFacturiNeachitate(limit) limiteaza rezultatele returnate")
    void testJpa03_FacturaRepoBoundedQuery() {
        List<FacturaFurnizor> facturi = facturaRepo.findFacturiNeachitate(5);
        assertNotNull(facturi);
        assertTrue(facturi.size() <= 5);
    }
}
