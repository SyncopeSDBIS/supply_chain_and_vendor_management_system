package ro.uaic.feaa.sdbis.supplychain.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event.ComandaAnulataEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Sprint 2 - Workflow Rules Tests (Atomic Cascading Cancellation, Partial Reception)")
public class WorkflowRulesTest {

    @Test
    @DisplayName("Cascading Cancellation: anuleazaComanda() atomically cancels order and unreceived lots")
    void testCascadingCancellation() {
        CodMaterial mat = CodMaterial.of("OTEL-01");
        LinieComanda linie = new LinieComanda(mat, Cantitate.of(100.0, "TO"), PretUnitar.of(500.0, "EUR"));
        ComandaAprovizionare comanda = new ComandaAprovizionare(
            IdComanda.random(), "CMD-2026-001", IdFurnizor.random(), UUID.randomUUID(),
            LocalDate.now(), LocalDate.now().plusDays(20), "DEP-A", Collections.singletonList(linie)
        );

        comanda.confirma(LocalDate.now().plusDays(15));
        IdLot lot1 = comanda.planificaLot("LOT-01", mat, Cantitate.of(40.0, "TO"), LocalDate.now(), LocalDate.now().plusYears(1));
        IdLot lot2 = comanda.planificaLot("LOT-02", mat, Cantitate.of(60.0, "TO"), LocalDate.now(), LocalDate.now().plusYears(1));

        // lot1 is received
        comanda.inregistreazaExpediere("DHL", "AWB123", Collections.singleton(lot1));
        comanda.inregistreazaReceptie(lot1, Cantitate.of(40.0, "TO"));
        assertEquals(StareComanda.ReceptionatPartial, comanda.getStareComanda());

        // Cancel order: lot2 (Planificat) must become Anulat, while lot1 (SositInDepozit) remains unchanged
        ComandaAnulataEvent event = comanda.anuleazaComanda();
        assertNotNull(event);
        assertEquals(StareComanda.Anulat, comanda.getStareComanda());

        Lot l1 = comanda.getLoturi().stream().filter(l -> l.getIdLot().equals(lot1)).findFirst().get();
        Lot l2 = comanda.getLoturi().stream().filter(l -> l.getIdLot().equals(lot2)).findFirst().get();

        assertEquals(StareLot.SositInDepozit, l1.getStareLot(), "Received lot must remain SositInDepozit");
        assertEquals(StareLot.Anulat, l2.getStareLot(), "Unreceived lot must be Anulat");
    }

    @Test
    @DisplayName("Order state derived from reception: ReceptionatPartial vs Receptionat")
    void testOrderReceptionTransitions() {
        CodMaterial mat = CodMaterial.of("LEMN-01");
        LinieComanda linie = new LinieComanda(mat, Cantitate.of(100.0, "MC"), PretUnitar.of(200.0, "RON"));
        ComandaAprovizionare comanda = new ComandaAprovizionare(
            IdComanda.random(), "CMD-2026-002", IdFurnizor.random(), UUID.randomUUID(),
            LocalDate.now(), LocalDate.now().plusDays(10), "DEP-B", Collections.singletonList(linie)
        );

        comanda.confirma(LocalDate.now().plusDays(8));
        IdLot lot = comanda.planificaLot("LOT-L1", mat, Cantitate.of(100.0, "MC"), LocalDate.now(), LocalDate.now().plusYears(2));
        comanda.inregistreazaExpediere("FAN", "AWB999", Collections.singleton(lot));

        // Partial reception: 60 out of 100
        comanda.inregistreazaReceptie(lot, Cantitate.of(60.0, "MC"));
        assertEquals(StareComanda.ReceptionatPartial, comanda.getStareComanda());

        // Full coverage: remaining 40 received
        comanda.inregistreazaReceptie(lot, Cantitate.of(40.0, "MC"));
        assertEquals(StareComanda.Receptionat, comanda.getStareComanda());
    }
}
