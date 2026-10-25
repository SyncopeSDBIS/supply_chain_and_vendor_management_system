package ro.uaic.feaa.sdbis.supplychain.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.*;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.factory.FurnizorFactory;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.factory.InspectieCalitateFactory;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.factory.ComandaFactory;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.factory.FacturaFactory;
import ro.uaic.feaa.sdbis.supplychain.shared.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Sprint 2 - Workflow Services & State Transitions Tests (ADR-003)")
public class WorkflowServicesTest {

    @Test
    @DisplayName("Workflow Comenzi: Flux complet de la Solicitat -> Confirmat -> InTranzit -> ReceptionatPartial -> Receptionat")
    void testComandaLifecycleWorkflow_Success() {
        CodMaterial mat = CodMaterial.of("TABLA-01");
        LinieComanda linie = new LinieComanda(mat, Cantitate.of(200.0, "MP"), PretUnitar.of(45.0, "RON"));

        // 1. Creare comanda (initial: Solicitat)
        ComandaAprovizionare comanda = ComandaFactory.creeaza(
            "CMD-WF-001", IdFurnizor.random(), UUID.randomUUID(),
            LocalDate.now(), LocalDate.now().plusDays(14), "DEP-1", List.of(linie)
        );
        assertEquals(StareComanda.Solicitat, comanda.getStareComanda());

        // 2. Confirmare furnizor
        comanda.confirma(LocalDate.now().plusDays(10));
        assertEquals(StareComanda.Confirmat, comanda.getStareComanda());

        // 3. Planificare lot si expediere (InTranzit)
        IdLot lot1 = comanda.planificaLot("LOT-T1", mat, Cantitate.of(100.0, "MP"), LocalDate.now(), LocalDate.now().plusYears(1));
        IdLot lot2 = comanda.planificaLot("LOT-T2", mat, Cantitate.of(100.0, "MP"), LocalDate.now(), LocalDate.now().plusYears(1));
        comanda.inregistreazaExpediere("DPD", "AWB-WF-1", Set.of(lot1, lot2));
        assertEquals(StareComanda.InTranzit, comanda.getStareComanda());

        // 4. Receptie partiala lot 1 -> ReceptionatPartial
        comanda.inregistreazaReceptie(lot1, Cantitate.of(100.0, "MP"));
        assertEquals(StareComanda.ReceptionatPartial, comanda.getStareComanda());
        assertFalse(comanda.esteAcoperitaIntegral());

        // 5. Receptie finala lot 2 -> Receptionat
        comanda.inregistreazaReceptie(lot2, Cantitate.of(100.0, "MP"));
        assertEquals(StareComanda.Receptionat, comanda.getStareComanda());
        assertTrue(comanda.esteAcoperitaIntegral());
    }

    @Test
    @DisplayName("Workflow Calitate: Flux calificare furnizor si inspectie AQL cu verdict")
    void testCalitateWorkflow_InspectieSiVerdict() {
        // 1. Inregistrare furnizor candidat
        Furnizor furnizor = FurnizorFactory.creeaza(
            "RO998877", "Furnizor Otel SA", new Adresa("Calea Chisinaului", "Iasi", "700000", "Romania"),
            Set.of(new Email("office@otel.ro")), new Iban("RO49AAAA1B31007593840000"),
            Set.of("METALURGIE"), new ConditiiContractuale(30, Currency.getInstance("RON"), Collections.emptyMap())
        );
        assertEquals(StareFurnizor.Candidat, furnizor.getStareFurnizor());

        // 2. Trimitere la evaluare si aprobare
        furnizor.trimiteLaEvaluare();
        assertEquals(StareFurnizor.InEvaluare, furnizor.getStareFurnizor());
        furnizor.aproba();
        assertEquals(StareFurnizor.Aprobat, furnizor.getStareFurnizor());
        assertTrue(furnizor.esteEligibilPentruComenzi());

        // 3. Programare inspectie AQL
        PlanAQL plan = PlanAQL.standard(1.5, 80, 2); // Ac=2, Re=3
        InspectieCalitate insp = InspectieCalitateFactory.programeaza(
            IdLot.random(), IdComanda.random(), furnizor.getIdFurnizor(),
            CodMaterial.of("OTEL-01"), plan, Perioada.lunaCurenta()
        );
        assertEquals(StareInspectie.Programata, insp.getStareInspectie());

        // 4. Alocare inspector si incepere inspectie
        insp.aloca(UUID.randomUUID());
        insp.incepe();
        assertEquals(StareInspectie.InDesfasurare, insp.getStareInspectie());

        insp.inregistreazaVerdict(1);
        assertEquals(Verdict.Conforma, insp.getVerdict());
        insp.inchide();
        assertEquals(StareInspectie.Inchisa, insp.getStareInspectie());
    }

    @Test
    @DisplayName("Workflow Facturi & Plati: Inregistrare -> Verificare -> Aprobare -> Ordin de Plata -> Executie")
    void testFacturiWorkflow_PlataIntegrala() {
        IdComanda comandaId = IdComanda.random();
        IdFurnizor furnizorId = IdFurnizor.random();

        LinieFactura lf = new LinieFactura(
            CodMaterial.of("MAT-01"), BigDecimal.valueOf(10), "BUC",
            new Suma(BigDecimal.valueOf(100), Currency.getInstance("RON")), BigDecimal.valueOf(0.19)
        );
        // Total: 1000 + 190 TVA = 1190 RON
        FacturaFurnizor factura = FacturaFactory.creeaza(
            "FC", "WF-101", LocalDate.now(), LocalDate.now().plusDays(30),
            comandaId, furnizorId, List.of(lf), Currency.getInstance("RON")
        );
        assertEquals(StareFactura.Inregistrata, factura.getStareFactura());

        // 1. Verificare si Aprobare
        factura.trimiteLaVerificare();
        assertEquals(StareFactura.InVerificare, factura.getStareFactura());
        factura.aproba();
        assertEquals(StareFactura.AprobataLaPlata, factura.getStareFactura());

        // 2. Emitere Ordin de Plata pentru suma integrala (1190 RON)
        UUID operatorId = UUID.randomUUID();
        Suma sumaPlata = new Suma(BigDecimal.valueOf(1190), Currency.getInstance("RON"));
        OrdinPlata op = factura.creeazaOrdinPlata(sumaPlata, operatorId);
        assertNotNull(op);
        assertEquals(StareOrdinPlata.Creat, op.getStareOrdin());

        // 3. Aprobare ordin si transmitere la banca
        UUID aprobatorId = UUID.randomUUID();
        op.aproba(aprobatorId);
        assertEquals(StareOrdinPlata.Aprobat, op.getStareOrdin());

        op.transmiteLaBanca();
        assertEquals(StareOrdinPlata.TransmisLaBanca, op.getStareOrdin());

        // 4. Confirmare executie si actualizare factura
        op.inregistreazaExecutie();
        assertEquals(StareOrdinPlata.Executat, op.getStareOrdin());

        factura.inregistreazaPlata(sumaPlata);
        assertEquals(StareFactura.Platita, factura.getStareFactura());
        assertEquals(BigDecimal.ZERO.setScale(2), factura.calculeazaRestDePlata().getValoare());
    }

    @Test
    @DisplayName("Workflow Alternativ: Suspendare furnizor la defecte consecutive si blocare plati")
    void testWorkflowAlternativ_SuspendareSiBlocare() {
        Furnizor f = FurnizorFactory.creeaza(
            "RO555666", "Furnizor Risc SA", new Adresa("S1", "Iasi", "700000", "RO"),
            Set.of(new Email("risc@otel.ro")), new Iban("RO49AAAA1B31007593840000"),
            Set.of("CHIMICE"), new ConditiiContractuale(30, Currency.getInstance("RON"), Collections.emptyMap())
        );
        f.trimiteLaEvaluare();
        f.aproba();
        assertEquals(StareFurnizor.Aprobat, f.getStareFurnizor());

        // Scorecard cu Calificativ C -> trece furnizorul in stare Conditionat
        ScorecardLunar scC = new ScorecardLunar(
            UUID.randomUUID(), Perioada.ofLuna(2026, 1),
            BigDecimal.valueOf(70), BigDecimal.valueOf(5), BigDecimal.valueOf(70),
            Scor.ponderat(BigDecimal.valueOf(70), BigDecimal.valueOf(70), BigDecimal.valueOf(70))
        );
        f.aplicaScorecard(scC);
        assertEquals(StareFurnizor.Conditionat, f.getStareFurnizor());

        // Inregistrare 2 inspectii neconforme consecutive in stare Conditionat -> devine Suspendat
        f.inregistreazaRezultatInspectie(Verdict.Neconforma);
        f.inregistreazaRezultatInspectie(Verdict.Neconforma);
        assertEquals(StareFurnizor.Suspendat, f.getStareFurnizor());
        assertFalse(f.esteEligibilPentruComenzi());
    }
}
