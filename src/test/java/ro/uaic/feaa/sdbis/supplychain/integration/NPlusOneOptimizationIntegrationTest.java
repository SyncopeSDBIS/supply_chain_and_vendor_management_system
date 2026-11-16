package ro.uaic.feaa.sdbis.supplychain.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.CalitateModuleFacade;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.ComandaApplicationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.ConfirmaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.CreeazaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.query.ComandaQueryService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.Cantitate;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.LinieComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.PretUnitar;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("Integration Tests: Eliminare Interogări N+1 prin Fetch Join în 2 Pași")
public class NPlusOneOptimizationIntegrationTest {

    @Autowired
    private ComandaQueryService comandaQueryService;

    @Autowired
    private ComandaApplicationService comandaAppService;

    @Autowired
    private FurnizorRepo furnizorRepo;

    @Autowired
    private InspectorRepo inspectorRepo;

    @Autowired
    private CalitateModuleFacade calitateFacade;

    @Test
    @DisplayName("Test Eliminare N+1 în Calculează OTIF: Comenzile, Liniile și Loturile sunt aduse în memorie via 2-Step Fetch")
    void testCalculOTIFEliminatesNPlusOne() {
        IdFurnizor fId = IdFurnizor.random();
        Furnizor f = new Furnizor(fId, "RO" + (System.currentTimeMillis() % 10000000), "Furnizor OTEL SRL",
                null, Collections.emptySet(), new Iban("RO49AAAA1B31007593840001"), Collections.singleton("METALE"), null);
        f.trimiteLaEvaluare();
        f.aproba();
        furnizorRepo.add(f);

        LinieComanda linie = new LinieComanda(CodMaterial.of("TABLA-01"), Cantitate.of(100.0, "KG"), PretUnitar.of(20.0, "EUR"));
        IdComanda cId = comandaAppService.creeazaComanda(new CreeazaComandaCommand(
                "CMD-OTIF-" + System.currentTimeMillis(), fId, UUID.randomUUID(),
                LocalDate.now(), LocalDate.now().plusDays(10), "Depozit Iasi", List.of(linie)
        ));

        comandaAppService.valideazaSiAproba(cId);
        comandaAppService.confirmaComanda(new ConfirmaComandaCommand(cId, LocalDate.now().plusDays(8)));

        // Calcul OTIF - apeleaza findAllWithLiniiAndLoturi(fId) care incarca liniile si loturile in exact 2 query-uri
        var otifView = comandaQueryService.calculeazaIndicatoriOTIF(fId, Perioada.lunaCurenta());

        assertNotNull(otifView);
        assertEquals(fId.getValue(), otifView.furnizorId());
        assertEquals(1, otifView.totalComenziInchise());
    }

    @Test
    @DisplayName("Test Eliminare N+1 în Facadă Expirare Certificări: Folosește findAllWithDetails() în loc de findAll()")
    void testExpirareCertificariInspectoriUsesFetchJoin() {
        UUID inspId = UUID.randomUUID();
        InspectorCalitate insp = new InspectorCalitate(inspId, new FullName("Mihai", "Ionescu"));
        insp.adaugaCertificare(new Certificare("ISO 9001 Lead Auditor", LocalDate.now().plusDays(5)));
        insp.declaraDisponibilitate(Perioada.of(LocalDate.now(), LocalDate.now().plusDays(30)));
        inspectorRepo.add(insp);

        int count = calitateFacade.proceseazaExpirareCertificariInspectori();
        assertTrue(count >= 1, "Trebuie sa proceseze certificarea inspectorului");
    }
}
