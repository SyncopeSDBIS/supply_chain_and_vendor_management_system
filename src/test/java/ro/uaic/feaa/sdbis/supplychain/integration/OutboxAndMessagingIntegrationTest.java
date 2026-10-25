package ro.uaic.feaa.sdbis.supplychain.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.InspectieRepo;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.ComandaApplicationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.CreeazaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.InregistreazaExpediereCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.InregistreazaReceptieCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.Cantitate;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.LinieComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.PretUnitar;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.NIR;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.NIRRepo;
import ro.uaic.feaa.sdbis.supplychain.infrastructure.messaging.OutboxDispatcherService;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class OutboxAndMessagingIntegrationTest {

    @Autowired
    private ComandaApplicationService comandaAppService;

    @Autowired
    private OutboxDispatcherService outboxDispatcher;

    @Autowired
    private NIRRepo nirRepo;

    @Autowired
    private InspectieRepo inspectieRepo;

    @Autowired
    private ro.uaic.feaa.sdbis.supplychain.calitate.domain.FurnizorRepo furnizorRepo;

    @Test
    @DisplayName("Test EDA: Receptia comenzii genereaza NIR si Inspectie prin Outbox Dispatcher cu garantie de Idempotenta")
    void testEventDrivenReceptionAndIdempotency() {
        IdFurnizor furnizorId = IdFurnizor.random();
        ro.uaic.feaa.sdbis.supplychain.calitate.domain.Furnizor f = new ro.uaic.feaa.sdbis.supplychain.calitate.domain.Furnizor(
            furnizorId, "RO" + (System.currentTimeMillis() % 10000000), "Furnizor Test EDA",
            null, Collections.emptySet(), new ro.uaic.feaa.sdbis.supplychain.calitate.domain.Iban("RO49AAAA1B31007593840000"),
            Collections.singleton("METALE"), null
        );
        f.trimiteLaEvaluare();
        f.aproba();
        furnizorRepo.add(f);

        UUID cumparatorId = UUID.randomUUID();
        String nrComanda = "CMD-EDA-" + System.currentTimeMillis();

        LinieComanda linie = new LinieComanda(
            CodMaterial.of("OTEL-INOX-316"),
            Cantitate.of(100.0, "KG"),
            PretUnitar.of(25.0, "EUR")
        );

        CreeazaComandaCommand createCmd = new CreeazaComandaCommand(
            nrComanda, furnizorId, cumparatorId,
            LocalDate.now(), LocalDate.now().plusDays(10), "Depozit Iasi",
            Collections.singletonList(linie)
        );

        IdComanda comandaId = comandaAppService.creeazaComanda(createCmd);

        // Confirma comanda de catre furnizor (tranzitie Solicitat -> Confirmat)
        comandaAppService.confirmaComanda(new ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.ConfirmaComandaCommand(
            comandaId, LocalDate.now().plusDays(7)
        ));

        // Planifica un lot
        IdLot lotId = comandaAppService.planificaLot(
            comandaId, "LOT-EDA-1", CodMaterial.of("OTEL-INOX-316"),
            Cantitate.of(100.0, "KG"), LocalDate.now(), LocalDate.now().plusYears(2)
        );

        // Inregistreaza expediere
        comandaAppService.inregistreazaExpediere(new InregistreazaExpediereCommand(
            comandaId, "FAN Courier", "AWB-EDA-999", Collections.singleton(lotId)
        ));

        // Inregistreaza receptie (salveaza ComandaReceptionataEvent in Outbox)
        comandaAppService.inregistreazaReceptie(new InregistreazaReceptieCommand(
            comandaId, lotId, Cantitate.of(100.0, "KG")
        ));

        // Dispatch evenimente nepublicate
        int dispatched = outboxDispatcher.dispatchUnpublishedEvents();
        assertTrue(dispatched >= 1, "Trebuie sa existe cel putin un eveniment expediat din Outbox");

        // Verifica faptul ca FacturaEventListener a generat NIR-ul
        List<NIR> niruri = nirRepo.findByComandaId(comandaId);
        assertFalse(niruri.isEmpty(), "NIR-ul trebuie sa fie generat automat in contextul Facturi");
        NIR nir = niruri.get(0);
        assertEquals(comandaId, nir.getComandaId());

        // Test Idempotenta: daca redispeceram, numarul de NIR-uri pentru aceeasi comanda ramane 1!
        outboxDispatcher.dispatchUnpublishedEvents();
        List<NIR> niruriDupaRedispecere = nirRepo.findByComandaId(comandaId);
        assertEquals(1, niruriDupaRedispecere.size(), "Idempotenta incalcata: a fost creat mai mult de un NIR!");
    }
}
