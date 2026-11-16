package ro.uaic.feaa.sdbis.supplychain.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.query.CalitateQueryService;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.*;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.query.ComandaQueryService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.query.FacturaQueryService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Currency;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class QueryServicesTest {

    @Autowired
    private ComandaQueryService comandaQueryService;

    @Autowired
    private CalitateQueryService calitateQueryService;

    @Autowired
    private FacturaQueryService facturaQueryService;

    @Autowired
    private ComandaRepo comandaRepo;

    @Autowired
    private FurnizorRepo furnizorRepo;

    @Autowired
    private FacturaRepo facturaRepo;

    @Test
    @DisplayName("Test CQRS Read Models: Interogari pentru Dashboard Livrari, Monitorizare Furnizori si Scadentar")
    void testQueryServices() {
        // Seed furnizor
        IdFurnizor furnizorId = IdFurnizor.random();
        Furnizor furnizor = new Furnizor(
                furnizorId,
                "RO" + System.currentTimeMillis(),
                "Furnizor Test Query SRL",
                null,
                Set.of(new Email("contact@testfurnizor.ro")),
                new Iban("RO98BTRL0000000000000001"),
                Set.of("MAT-01"),
                null
        );
        furnizor.trimiteLaEvaluare();
        furnizor.aproba();
        furnizorRepo.add(furnizor);

        // Seed comanda
        IdComanda comandaId = IdComanda.random();
        ComandaAprovizionare comanda = new ComandaAprovizionare(
                comandaId,
                "CMD-QUERY-01",
                furnizorId,
                UUID.randomUUID(),
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(5),
                "Depozit Central",
                Collections.singletonList(new LinieComanda(CodMaterial.of("MAT-01"), Cantitate.of(10.0, "BUC"), PretUnitar.of(100.0, "RON")))
        );
        comandaRepo.add(comanda);

        // Seed factura
        FacturaFurnizor factura = new FacturaFurnizor(
                UUID.randomUUID(), "FC", "1001", LocalDate.now(), LocalDate.now().plusDays(10),
                comandaId, furnizorId,
                Collections.singletonList(new LinieFactura(CodMaterial.of("MAT-01"), BigDecimal.TEN, "BUC", Suma.of(100.0, "RON"), BigDecimal.valueOf(0.19))),
                Currency.getInstance("RON")
        );
        factura.trimiteLaVerificare();
        factura.aproba();
        facturaRepo.add(factura);

        // Interogare CQRS
        var dashboard = comandaQueryService.obtineDashboardLivrari(null);
        assertNotNull(dashboard, "Dashboard livrari nu trebuie sa fie null");
        assertFalse(dashboard.isEmpty(), "Dashboard livrari trebuie sa contina inregistrari");

        var otif = comandaQueryService.calculeazaIndicatoriOTIF(furnizorId, Perioada.lunaCurenta());
        assertNotNull(otif, "Indicatori OTIF nu trebuie sa fie null");
        assertEquals(furnizorId.getValue(), otif.furnizorId(), "Id-ul furnizorului din raportul OTIF trebuie sa coincida");

        var furnizori = calitateQueryService.obtineMonitorizareFurnizori();
        assertNotNull(furnizori, "Monitorizare furnizori nu trebuie sa fie null");
        assertFalse(furnizori.isEmpty(), "Lista de monitorizare furnizori nu trebuie sa fie goala");

        var scadentar = facturaQueryService.obtineScadentar();
        assertNotNull(scadentar, "Scadentar facturi nu trebuie sa fie null");
        assertFalse(scadentar.isEmpty(), "Scadentarul facturilor trebuie sa contina factura aprobata");
    }
}
