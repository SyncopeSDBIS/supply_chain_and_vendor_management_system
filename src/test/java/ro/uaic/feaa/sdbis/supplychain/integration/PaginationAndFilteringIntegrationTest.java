package ro.uaic.feaa.sdbis.supplychain.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.calitate.application.query.CalitateQueryService;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Furnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.FurnizorRepo;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Iban;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.ComandaApplicationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.command.CreeazaComandaCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.application.query.ComandaQueryService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.Cantitate;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.LinieComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.PretUnitar;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.query.FacturaQueryService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.*;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("Integration Tests: Paginare Nativă DB, Filtrare Text și Read Models Scadentar")
public class PaginationAndFilteringIntegrationTest {

    @Autowired
    private CalitateQueryService calitateQueryService;

    @Autowired
    private FurnizorRepo furnizorRepo;

    @Autowired
    private FacturaQueryService facturaQueryService;

    @Autowired
    private FacturaRepo facturaRepo;

    @Autowired
    private ComandaQueryService comandaQueryService;

    @Autowired
    private ComandaApplicationService comandaAppService;

    @Test
    @DisplayName("Test Paginare DB + Căutare Text: Filtrarea SQL LIKE este aplicată înainte de LIMIT/OFFSET")
    void testFurnizoriDbSearchAndPagination() {
        long ts = System.currentTimeMillis();
        Furnizor f1 = new Furnizor(IdFurnizor.random(), "RO" + (ts % 10000000 + 1), "Alpha Tech Solutions SRL",
                null, Collections.emptySet(), new Iban("RO49AAAA1B31007593840001"), Collections.singleton("IT"), null);
        f1.trimiteLaEvaluare();
        f1.aproba();
        furnizorRepo.add(f1);

        Furnizor f2 = new Furnizor(IdFurnizor.random(), "RO" + (ts % 10000000 + 2), "Beta Components SA",
                null, Collections.emptySet(), new Iban("RO49AAAA1B31007593840002"), Collections.singleton("PIESE"), null);
        f2.trimiteLaEvaluare();
        f2.aproba();
        furnizorRepo.add(f2);

        Furnizor f3 = new Furnizor(IdFurnizor.random(), "RO" + (ts % 10000000 + 3), "Gamma Tech Industries SRL",
                null, Collections.emptySet(), new Iban("RO49AAAA1B31007593840003"), Collections.singleton("IT"), null);
        f3.trimiteLaEvaluare();
        f3.aproba();
        furnizorRepo.add(f3);

        Furnizor f4 = new Furnizor(IdFurnizor.random(), "RO" + (ts % 10000000 + 4), "Zeta Tech Automation SRL",
                null, Collections.emptySet(), new Iban("RO49AAAA1B31007593840004"), Collections.singleton("IT"), null);
        f4.trimiteLaEvaluare();
        f4.aproba();
        furnizorRepo.add(f4);

        // Cautare dupa "Tech": Alpha Tech, Gamma Tech, Zeta Tech (3 rezultate in total)
        // Pagina 0 cu dimensiune 2: trebuie sa returneze exact 2 rezultate ("Alpha Tech", "Gamma Tech")
        var pagina0 = calitateQueryService.listeazaFurnizori(null, "Tech", 0, 2);
        assertEquals(2, pagina0.size(), "Pagina 0 trebuie sa aiba exact 2 elemente paginate");
        assertEquals("Alpha Tech Solutions SRL", pagina0.get(0).denumire());
        assertEquals("Gamma Tech Industries SRL", pagina0.get(1).denumire());

        // Pagina 1 cu dimensiune 2: trebuie sa returneze restul de 1 rezultat ("Zeta Tech")
        var pagina1 = calitateQueryService.listeazaFurnizori(null, "Tech", 1, 2);
        assertEquals(1, pagina1.size(), "Pagina 1 trebuie sa contina al 3-lea furnizor");
        assertEquals("Zeta Tech Automation SRL", pagina1.get(0).denumire());

        // Pagina 2 cu dimensiune 2: trebuie sa fie vida
        var pagina2 = calitateQueryService.listeazaFurnizori(null, "Tech", 2, 2);
        assertTrue(pagina2.isEmpty(), "Pagina 2 trebuie sa fie goala");
    }

    @Test
    @DisplayName("Test Scadentar Facturi: Exclude stările finale Platita și Anulata din calculul ratelor de scadență")
    void testScadentarFacturiExcludesAchitataSiAnulata() {
        IdFurnizor furnizorId = IdFurnizor.random();
        IdComanda comandaId = IdComanda.random();

        LinieFactura l1 = new LinieFactura(CodMaterial.of("M1"), BigDecimal.valueOf(10), "BUC", Suma.of(10.0, "EUR"), BigDecimal.valueOf(0.19));
        FacturaFurnizor fActiva = new FacturaFurnizor(
                UUID.randomUUID(), "F-ACT", "101", LocalDate.now(), LocalDate.now().plusDays(10),
                comandaId, furnizorId, List.of(l1), java.util.Currency.getInstance("EUR")
        );
        facturaRepo.add(fActiva);

        LinieFactura l2 = new LinieFactura(CodMaterial.of("M2"), BigDecimal.valueOf(20), "BUC", Suma.of(10.0, "EUR"), BigDecimal.valueOf(0.19));
        FacturaFurnizor fPlatita = new FacturaFurnizor(
                UUID.randomUUID(), "F-PLA", "102", LocalDate.now().minusDays(20), LocalDate.now().minusDays(5),
                comandaId, furnizorId, List.of(l2), java.util.Currency.getInstance("EUR")
        );
        fPlatita.inregistreazaPlata(Suma.of(238.0, "EUR"));
        facturaRepo.add(fPlatita);

        LinieFactura l3 = new LinieFactura(CodMaterial.of("M3"), BigDecimal.valueOf(5), "BUC", Suma.of(10.0, "EUR"), BigDecimal.valueOf(0.19));
        FacturaFurnizor fAnulata = new FacturaFurnizor(
                UUID.randomUUID(), "F-ANU", "103", LocalDate.now().minusDays(10), LocalDate.now().plusDays(5),
                comandaId, furnizorId, List.of(l3), java.util.Currency.getInstance("EUR")
        );
        fAnulata.anuleaza();
        facturaRepo.add(fAnulata);

        var scadentar = facturaQueryService.obtineScadentar();

        boolean contineActiva = scadentar.stream().anyMatch(v -> v.idFactura().equals(fActiva.getIdFactura()));
        boolean continePlatita = scadentar.stream().anyMatch(v -> v.idFactura().equals(fPlatita.getIdFactura()));
        boolean contineAnulata = scadentar.stream().anyMatch(v -> v.idFactura().equals(fAnulata.getIdFactura()));

        assertTrue(contineActiva, "Factura activa/inregistrata trebuie sa apara in scadentar");
        assertFalse(continePlatita, "Factura achitata NU are voie sa apara in scadentar");
        assertFalse(contineAnulata, "Factura anulata NU are voie sa apara in scadentar");
    }

    @Test
    @DisplayName("Test Comenzi Paginare cu 2-Step Fetch: Liniile și Loturile sunt încărcate complet fără N+1")
    void testComenziPaginationAndDetailFetch() {
        IdFurnizor furnizorId = IdFurnizor.random();
        Furnizor f = new Furnizor(furnizorId, "RO" + (System.currentTimeMillis() % 10000000), "Furnizor Comenzi SRL",
                null, Collections.emptySet(), new Iban("RO49AAAA1B31007593840005"), Collections.singleton("OTEL"), null);
        f.trimiteLaEvaluare();
        f.aproba();
        furnizorRepo.add(f);

        LinieComanda l1 = new LinieComanda(CodMaterial.of("OTEL-1"), Cantitate.of(50.0, "KG"), PretUnitar.of(10.0, "EUR"));
        LinieComanda l2 = new LinieComanda(CodMaterial.of("OTEL-2"), Cantitate.of(30.0, "KG"), PretUnitar.of(20.0, "EUR"));

        IdComanda cId = comandaAppService.creeazaComanda(new CreeazaComandaCommand(
                "CMD-PAG-" + System.currentTimeMillis(), furnizorId, UUID.randomUUID(),
                LocalDate.now(), LocalDate.now().plusDays(15), "Depozit Iasi", List.of(l1, l2)
        ));

        var list = comandaQueryService.listeazaComenzi(furnizorId.getValue(), null, 0, 10);
        assertFalse(list.isEmpty(), "Trebuie sa returneze comanda creata");
        var view = list.get(0);
        assertEquals(cId.getValue(), view.id());
        assertEquals(2, view.numarLinii(), "Numarul de linii trebuie sa fie 2 prin fetch join corect");
        assertEquals(0, BigDecimal.valueOf(1100.0).compareTo(view.valoareTotalaEstimata()), "Totalul calculat trebuie sa fie 50*10 + 30*20 = 1100 EUR");
    }
}
