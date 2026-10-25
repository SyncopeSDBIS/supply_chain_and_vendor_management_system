package ro.uaic.feaa.sdbis.supplychain.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.query.CalitateQueryService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.query.ComandaQueryService;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.query.FacturaQueryService;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class QueryServicesTest {

    @Autowired
    private ComandaQueryService comandaQueryService;

    @Autowired
    private CalitateQueryService calitateQueryService;

    @Autowired
    private FacturaQueryService facturaQueryService;

    @Test
    @DisplayName("Test CQRS Read Models: Interogari pentru Dashboard Livrari, Monitorizare Furnizori si Scadentar")
    void testQueryServices() {
        var dashboard = comandaQueryService.obtineDashboardLivrari(null);
        assertNotNull(dashboard, "Dashboard livrari nu trebuie sa fie null");

        var otif = comandaQueryService.calculeazaIndicatoriOTIF(IdFurnizor.random(), Perioada.lunaCurenta());
        assertNotNull(otif, "Indicatori OTIF nu trebuie sa fie null");

        var furnizori = calitateQueryService.obtineMonitorizareFurnizori();
        assertNotNull(furnizori, "Monitorizare furnizori nu trebuie sa fie null");

        var scadentar = facturaQueryService.obtineScadentar();
        assertNotNull(scadentar, "Scadentar facturi nu trebuie sa fie null");
    }
}
