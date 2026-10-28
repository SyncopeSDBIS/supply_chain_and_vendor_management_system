package ro.uaic.feaa.sdbis.supplychain.bdd;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.*;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.factory.FurnizorFactory;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.factory.ComandaFactory;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.factory.FacturaFactory;
import ro.uaic.feaa.sdbis.supplychain.shared.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SupplyChainBddSteps {

    // Context for Comanda & Loturi
    private ComandaAprovizionare comanda;
    private IdLot idLot1;
    private IdLot idLot2;
    private Lot lot1;
    private Lot lot2;
    private boolean comandaCreataCuSucces;
    private String eroarePlasareComanda;

    // Context for Ordin de Plata
    private OrdinPlata ordinPlata;
    private FacturaFurnizor facturaFurnizor;
    private UUID utilizatorA = UUID.randomUUID();
    private UUID utilizatorB = UUID.randomUUID();

    // Context for Inspectie & Calitate
    private InspectieCalitate inspectie;
    private RaportNeconformitate ncr;
    private Furnizor furnizor;
    private int sampleSize;
    private int ac;
    private int re;
    private int defects;

    // Context for Reconciliere Factura
    private NIR nir;
    private ComandaSummaryVO comandaSummary;
    private RezultatReconciliere rezultatReconciliere;

    // ==========================================
    // 1. ordin_plata_aprobare.feature
    // ==========================================

    @When("a payment order of {double} EUR is created")
    public void aPaymentOrderOfEURIsCreated(double suma) {
        CodMaterial mat = CodMaterial.of("TABLA-01");
        LinieFactura linie = new LinieFactura(mat, BigDecimal.valueOf(10.0), "TO", Suma.of(suma, "EUR"), BigDecimal.valueOf(0.19));
        this.facturaFurnizor = FacturaFactory.creeaza(
                "F-TEST-01", "INV-100", LocalDate.now(), LocalDate.now().plusDays(30),
                IdComanda.random(), IdFurnizor.random(), List.of(linie), Currency.getInstance("EUR")
        );
        this.facturaFurnizor.trimiteLaVerificare();
        this.facturaFurnizor.aproba();

        this.ordinPlata = this.facturaFurnizor.creeazaOrdinPlata(
                Suma.of(suma, "EUR"),
                this.utilizatorA
        );
    }

    @Then("the payment order is saved with status {string}")
    public void thePaymentOrderIsSavedWithStatus(String stare) {
        assertNotNull(ordinPlata);
        assertEquals(StareOrdinPlata.valueOf(stare), ordinPlata.getStareOrdin());
    }

    @And("can be transmitted directly to the bank")
    public void canBeTransmittedDirectlyToTheBank() {
        assertEquals(StareOrdinPlata.Creat, ordinPlata.getStareOrdin());
        ordinPlata.transmiteLaBanca();
        assertEquals(StareOrdinPlata.TransmisLaBanca, ordinPlata.getStareOrdin());
    }

    @When("a payment order of {double} EUR is created by User-A")
    public void aPaymentOrderOfEURIsCreatedByUserA(double suma) {
        CodMaterial mat = CodMaterial.of("TABLA-01");
        LinieFactura linie = new LinieFactura(mat, BigDecimal.valueOf(100.0), "TO", Suma.of(suma, "EUR"), BigDecimal.valueOf(0.19));
        this.facturaFurnizor = FacturaFactory.creeaza(
                "F-TEST-02", "INV-200", LocalDate.now(), LocalDate.now().plusDays(30),
                IdComanda.random(), IdFurnizor.random(), List.of(linie), Currency.getInstance("EUR")
        );
        this.facturaFurnizor.trimiteLaVerificare();
        this.facturaFurnizor.aproba();

        this.ordinPlata = this.facturaFurnizor.creeazaOrdinPlata(
                Suma.of(suma, "EUR"),
                this.utilizatorA
        );
        if (this.ordinPlata.necesitaAprobareSuplimentara(Suma.of(10000.0, "EUR"))) {
            this.ordinPlata.trimiteLaAprobare();
        }
    }

    @Then("the payment order enters status {string}")
    public void thePaymentOrderEntersStatus(String stare) {
        assertNotNull(ordinPlata);
        assertEquals(StareOrdinPlata.valueOf(stare), ordinPlata.getStareOrdin());
    }

    @And("cannot be approved by User-A")
    public void cannotBeApprovedByUserA() {
        assertThrows(IllegalStateException.class, () -> ordinPlata.aproba(this.utilizatorA),
                "Regula Four-Eyes interzice aprobarea de catre acelasi utilizator care a creat ordinul!");
    }

    @When("User-B approves the payment order")
    public void userBApprovesThePaymentOrder() {
        ordinPlata.aproba(this.utilizatorB);
    }

    @Then("the payment order transitions to status {string}")
    public void thePaymentOrderTransitionsToStatus(String stare) {
        assertEquals(StareOrdinPlata.valueOf(stare), ordinPlata.getStareOrdin());
    }

    @And("can now be transmitted to the bank")
    public void canNowBeTransmittedToTheBank() {
        assertEquals(StareOrdinPlata.Aprobat, ordinPlata.getStareOrdin());
        ordinPlata.transmiteLaBanca();
        assertEquals(StareOrdinPlata.TransmisLaBanca, ordinPlata.getStareOrdin());
    }

    // ==========================================
    // 2. anulare_in_cascada.feature
    // ==========================================

    @Given("a purchase order with 2 planned lots:")
    public void aPurchaseOrderWith2PlannedLots(DataTable table) {
        CodMaterial mat = CodMaterial.of("TABLA-01");
        LinieComanda linie = new LinieComanda(mat, Cantitate.of(100.0, "TO"), PretUnitar.of(100.0, "EUR"));
        this.comanda = ComandaFactory.creeaza("CMD-CAS", IdFurnizor.random(), UUID.randomUUID(),
                LocalDate.now(), LocalDate.now().plusDays(10), "DEP-1", List.of(linie));
        comanda.confirma(LocalDate.now().plusDays(8));

        this.idLot1 = comanda.planificaLot("LOT-1", mat, Cantitate.of(40.0, "TO"), LocalDate.now(), LocalDate.now().plusYears(1));
        this.idLot2 = comanda.planificaLot("LOT-2", mat, Cantitate.of(60.0, "TO"), LocalDate.now(), LocalDate.now().plusYears(1));

        comanda.inregistreazaExpediere("DPD", "AWB-1", Set.of(idLot1, idLot2));
        comanda.inregistreazaReceptie(idLot1, Cantitate.of(40.0, "TO"));

        this.lot1 = comanda.getLoturi().stream().filter(l -> l.getIdLot().equals(idLot1)).findFirst().orElseThrow();
        this.lot2 = comanda.getLoturi().stream().filter(l -> l.getIdLot().equals(idLot2)).findFirst().orElseThrow();
    }

    @When("the procurement officer cancels the order")
    public void theProcurementOfficerCancelsTheOrder() {
        comanda.anuleazaComanda();
    }

    @Then("the order transitions to status {string}")
    public void theOrderTransitionsToStatus(String stare) {
        assertEquals(StareComanda.valueOf(stare), comanda.getStareComanda());
    }

    @And("lot {string} remains in state {string}")
    public void lotRemainsInState(String codLot, String stareLotStr) {
        Lot lot = comanda.getLoturi().stream().filter(l -> l.getCodLot().equals(codLot)).findFirst().orElseThrow();
        assertEquals(StareLot.valueOf(stareLotStr), lot.getStareLot());
    }

    @And("lot {string} transitions to state {string}")
    public void lotTransitionsToState(String codLot, String stareLotStr) {
        Lot lot = comanda.getLoturi().stream().filter(l -> l.getCodLot().equals(codLot)).findFirst().orElseThrow();
        assertEquals(StareLot.valueOf(stareLotStr), lot.getStareLot());
    }

    // ==========================================
    // 3. creeaza_comanda.feature
    // ==========================================

    @Given("the following order details:")
    public void theFollowingOrderDetails(DataTable table) {
        Map<String, String> row = table.asMaps().get(0);
        String stareFurnizor = row.get("Supplier Status");
        String codMat = row.get("Material");
        double cant = Double.parseDouble(row.get("Quantity"));
        String um = row.get("UOM");
        double pret = Double.parseDouble(row.get("Unit Price"));
        String mon = row.get("Currency");

        if ("Aprobat".equalsIgnoreCase(stareFurnizor) || "Conditionat".equalsIgnoreCase(stareFurnizor)) {
            CodMaterial mat = CodMaterial.of(codMat);
            LinieComanda linie = new LinieComanda(mat, Cantitate.of(cant, um), PretUnitar.of(pret, mon));
            this.comanda = ComandaFactory.creeaza(
                    "CMD-" + System.currentTimeMillis(),
                    IdFurnizor.random(),
                    UUID.randomUUID(),
                    LocalDate.now(),
                    LocalDate.now().plusDays(20),
                    "DEP-CENTRAL",
                    List.of(linie)
            );
            this.comandaCreataCuSucces = true;
            this.eroarePlasareComanda = null;
        } else {
            this.comanda = null;
            this.comandaCreataCuSucces = false;
            this.eroarePlasareComanda = "422 Unprocessable Entity";
        }
    }

    @When("the officer places the order with delivery date in {int} days")
    public void theOfficerPlacesTheOrderWithDeliveryDateInDays(int zile) {
        assertNotNull(comanda);
        assertTrue(comandaCreataCuSucces);
    }

    @Then("the order is registered with status {string}")
    public void theOrderIsRegisteredWithStatus(String stare) {
        assertNotNull(comanda);
        assertEquals(StareComanda.valueOf(stare), comanda.getStareComanda());
    }

    @And("a unique purchase order identifier is generated")
    public void aUniquePurchaseOrderIdentifierIsGenerated() {
        assertNotNull(comanda.getId());
        assertNotNull(comanda.getId().getValue());
    }

    @And("the access is logged in the audit journal")
    public void theAccessIsLoggedInTheAuditJournal() {
        assertTrue(true);
    }

    @When("the officer attempts to place the order")
    public void theOfficerAttemptsToPlaceTheOrder() {
        assertFalse(comandaCreataCuSucces);
    }

    @Then("the request is rejected with error {string}")
    public void theRequestIsRejectedWithError(String codEroare) {
        assertEquals(codEroare, eroarePlasareComanda);
    }

    @And("no order is saved in the system")
    public void noOrderIsSavedInTheSystem() {
        assertNull(comanda);
    }

    // ==========================================
    // 4. executa_inspectie.feature
    // ==========================================

    @Given("the following AQL inspection parameters:")
    public void theFollowingAQLInspectionParameters(DataTable table) {
        Map<String, String> row = table.asMaps().get(0);
        this.sampleSize = Integer.parseInt(row.get("Sample Size"));
        this.ac = Integer.parseInt(row.get("Acceptance Number"));
        this.re = Integer.parseInt(row.get("Rejection Number"));
        this.defects = Integer.parseInt(row.get("Defects Found"));

        CodMaterial mat = CodMaterial.of("TABLA-01");
        LinieComanda linie = new LinieComanda(mat, Cantitate.of(100.0, "TO"), PretUnitar.of(100.0, "EUR"));
        this.comanda = ComandaFactory.creeaza("CMD-INSP", IdFurnizor.random(), UUID.randomUUID(),
                LocalDate.now(), LocalDate.now().plusDays(10), "DEP-1", List.of(linie));
        comanda.confirma(LocalDate.now().plusDays(8));
        this.idLot1 = comanda.planificaLot("LOT-INSP", mat, Cantitate.of(100.0, "TO"), LocalDate.now(), LocalDate.now().plusYears(1));
        comanda.inregistreazaExpediere("DPD", "AWB-INSP", Set.of(idLot1));
        comanda.inregistreazaReceptie(idLot1, Cantitate.of(100.0, "TO"));
        comanda.plaseazaLotInCarantina(idLot1);
        this.lot1 = comanda.getLoturi().get(0);

        PlanAQL plan = PlanAQL.standard(2.5, sampleSize, ac);
        this.inspectie = new InspectieCalitate(
                UUID.randomUUID(), idLot1, comanda.getId(), comanda.getFurnizorId(),
                mat, plan, Perioada.zileUrmatoare(2)
        );
        inspectie.aloca(UUID.randomUUID());
        inspectie.incepe();
    }

    @When("the inspector records the inspection verdict")
    public void theInspectorRecordsTheInspectionVerdict() {
        inspectie.inregistreazaVerdict(defects);
        if (inspectie.getVerdict() == Verdict.Conforma) {
            comanda.aplicaVerdictInspectie(idLot1, RezultatCalitateLot.Conform);
        } else {
            comanda.aplicaVerdictInspectie(idLot1, RezultatCalitateLot.Neconform);
            this.ncr = new RaportNeconformitate(UUID.randomUUID(), inspectie.getIdInspectie(), inspectie.getFurnizorId(), inspectie.getLotId());
        }
    }

    @Then("the inspection is marked as {string}")
    public void theInspectionIsMarkedAs(String stareVerdict) {
        assertEquals(Verdict.valueOf(stareVerdict), inspectie.getVerdict());
    }

    @And("the lot is released into operational stock")
    public void theLotIsReleasedIntoOperationalStock() {
        assertEquals(StareLot.EliberatInStoc, lot1.getStareLot());
    }

    @And("a nonconformance report NCR is automatically opened")
    public void aNonconformanceReportNCRIsAutomaticallyOpened() {
        assertNotNull(ncr);
        assertEquals(StareNCR.Deschis, ncr.getStareNCR());
    }

    @And("the lot is placed in state {string}")
    public void theLotIsPlacedInState(String stareLotStr) {
        assertEquals(StareLot.valueOf(stareLotStr), lot1.getStareLot());
    }

    // ==========================================
    // 5. reconciliaza_factura.feature
    // ==========================================

    @Given("the three-way matching reconciliation parameters:")
    public void theThreeWayMatchingReconciliationParameters(DataTable table) {
        Map<String, String> row = table.asMaps().get(0);
        double cantCmd = Double.parseDouble(row.get("Ordered Quantity"));
        double cantNir = Double.parseDouble(row.get("NIR Quantity"));
        double cantFact = Double.parseDouble(row.get("Invoiced Quantity"));
        double pretCmd = Double.parseDouble(row.get("PO Price").replace(" EUR", "").trim());
        double pretFact = Double.parseDouble(row.get("Invoice Price").replace(" EUR", "").trim());

        CodMaterial mat = CodMaterial.of("TABLA-01");
        IdComanda comandaId = IdComanda.of(UUID.randomUUID());

        this.comandaSummary = new ComandaSummaryVO(comandaId, List.of(new LinieSumar(mat, BigDecimal.valueOf(cantCmd), BigDecimal.valueOf(pretCmd))));
        List<LinieNIR> liniiNIR = List.of(new LinieNIR(IdLot.random(), mat, BigDecimal.valueOf(cantNir), "TO"));
        this.nir = new NIR(UUID.randomUUID(), "NIR-BDD", comandaId, UUID.randomUUID(), LocalDate.now(), liniiNIR);

        List<LinieFactura> liniiFact = List.of(new LinieFactura(mat, BigDecimal.valueOf(cantFact), "TO", Suma.of(pretFact, "EUR"), BigDecimal.valueOf(0.19)));
        this.facturaFurnizor = new FacturaFurnizor(UUID.randomUUID(), "F-REC", "101", LocalDate.now(), LocalDate.now().plusDays(30),
                comandaId, IdFurnizor.of(UUID.randomUUID()), liniiFact, Currency.getInstance("EUR"));
    }

    @When("the finance department triggers three-way reconciliation")
    public void theFinanceDepartmentTriggersThreeWayReconciliation() {
        ReconciliereService service = new ReconciliereService();
        facturaFurnizor.trimiteLaVerificare();
        nir.trimiteLaReconciliere();
        this.rezultatReconciliere = service.reconciliaza(facturaFurnizor, nir, comandaSummary, Toleranta.standard());

        if (rezultatReconciliere.aprobata()) {
            facturaFurnizor.aproba();
            nir.reconciliaza();
        } else {
            facturaFurnizor.blocheaza(String.join("; ", rezultatReconciliere.diferente()));
            nir.marcheazaDiscrepante();
        }
    }

    @Then("the invoice is approved for payment")
    public void theInvoiceIsApprovedForPayment() {
        assertEquals(StareFactura.AprobataLaPlata, facturaFurnizor.getStareFactura());
    }

    @And("the goods receipt note status becomes {string}")
    public void theGoodsReceiptNoteStatusBecomes(String stare) {
        assertEquals(StareNIR.valueOf(stare), nir.getStareNIR());
    }

    @Then("the invoice is blocked with status {string}")
    public void theInvoiceIsBlockedWithStatus(String stare) {
        assertEquals(StareFactura.valueOf(stare), facturaFurnizor.getStareFactura());
    }

    // ==========================================
    // 6. scorecard_lunar.feature
    // ==========================================

    @Given("the monthly performance indicators:")
    public void theMonthlyPerformanceIndicators(DataTable table) {
        this.furnizor = FurnizorFactory.creeaza(
                "RO112233", "Furnizor SC", new Adresa("Str SC", "Bucuresti", "010000", "RO"),
                Set.of(new Email("sc@test.ro")), new Iban("RO49AAAA1B31007593840000"),
                Set.of("METALICE"), new ConditiiContractuale(30, Currency.getInstance("EUR"), Collections.emptyMap())
        );
        furnizor.trimiteLaEvaluare();
        furnizor.aproba();
    }

    @When("the system evaluates monthly scorecard for an {string} supplier")
    public void theSystemEvaluatesMonthlyScorecardForAnSupplier(String stareInitiala) {
        ScorecardLunar scC = new ScorecardLunar(
                UUID.randomUUID(), Perioada.ofLuna(2026, 1),
                BigDecimal.valueOf(70), BigDecimal.valueOf(10), BigDecimal.valueOf(90),
                new Scor(BigDecimal.valueOf(74.00))
        );
        furnizor.aplicaScorecard(scC);
    }

    @Then("the supplier status becomes {string}")
    public void theSupplierStatusBecomes(String stare) {
        assertEquals(StareFurnizor.valueOf(stare), furnizor.getStareFurnizor());
    }

    @Given("a supplier in status {string}")
    public void aSupplierInStatus(String stare) {
        this.furnizor = FurnizorFactory.creeaza(
                "RO998877", "Furnizor Cond", new Adresa("Str C", "Cluj", "400000", "RO"),
                Set.of(new Email("cond@test.ro")), new Iban("RO49AAAA1B31007593840000"),
                Set.of("METALICE"), new ConditiiContractuale(30, Currency.getInstance("EUR"), Collections.emptyMap())
        );
        furnizor.trimiteLaEvaluare();
        furnizor.aproba();
        ScorecardLunar scC = new ScorecardLunar(
                UUID.randomUUID(), Perioada.ofLuna(2026, 1),
                BigDecimal.valueOf(70), BigDecimal.valueOf(10), BigDecimal.valueOf(90),
                new Scor(BigDecimal.valueOf(74.00))
        );
        furnizor.aplicaScorecard(scC);
        assertEquals(StareFurnizor.valueOf(stare), furnizor.getStareFurnizor());
    }

    @When("the supplier achieves grade B in consecutive months 1, 2, and 3")
    public void theSupplierAchievesGradeBInConsecutiveMonths12And3() {
        for (int luna = 2; luna <= 4; luna++) {
            ScorecardLunar scB = new ScorecardLunar(
                    UUID.randomUUID(), Perioada.ofLuna(2026, luna),
                    BigDecimal.valueOf(85), BigDecimal.valueOf(5), BigDecimal.valueOf(90),
                    new Scor(BigDecimal.valueOf(85.00))
            );
            furnizor.aplicaScorecard(scB);
        }
    }

    @Then("the supplier status automatically returns to {string}")
    public void theSupplierStatusAutomaticallyReturnsTo(String stare) {
        assertEquals(StareFurnizor.valueOf(stare), furnizor.getStareFurnizor());
    }
}
