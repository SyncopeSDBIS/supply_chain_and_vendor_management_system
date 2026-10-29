package ro.uaic.feaa.sdbis.supplychain.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Furnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.FurnizorRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Sprint 4 - REST API & Content Negotiation Tests (JSON & XML)")
public class ComandaCtrlRestTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FurnizorRepo furnizorRepo;

    @Test
    @DisplayName("POST /api/comenzi - Content Negotiation JSON returns 201 Created for approved supplier")
    void testCreateOrderJsonSuccess() throws Exception {
        IdFurnizor furnizorId = IdFurnizor.random();
        Furnizor f = new Furnizor(
            furnizorId,
            "RO" + System.currentTimeMillis(),
            "Furnizor Test SRL",
            null,
            null,
            new ro.uaic.feaa.sdbis.supplychain.calitate.domain.Iban("RO98BTRL0000000000000001"),
            java.util.Set.of("TABLA-01"),
            null
        );
        f.aproba();
        furnizorRepo.add(f);

        String json = """
            {
              "numarComanda": "CMD-TEST-JSON-%s",
              "furnizorId": "%s",
              "cumparatorId": "%s",
              "dataComanda": "2026-03-01",
              "dataLivrareCeruta": "2026-03-20",
              "depozit": "Depozit Central",
              "linii": [
                {
                  "materialCod": "TABLA-01",
                  "cantitate": 50.0,
                  "unitateMasura": "TO",
                  "pretUnitar": 120.0,
                  "moneda": "EUR"
                }
              ]
            }
            """.formatted(System.currentTimeMillis(), furnizorId.getValue(), UUID.randomUUID());

        mockMvc.perform(post("/api/comenzi")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("Solicitat"))
            .andExpect(jsonPath("$.comandaId").exists());
    }

    @Test
    @DisplayName("POST /api/comenzi - Ineligible supplier returns 422 Unprocessable Entity (Figura 25)")
    void testCreateOrderIneligibleSupplier() throws Exception {
        IdFurnizor furnizorId = IdFurnizor.random();
        Furnizor f = new Furnizor(
            furnizorId,
            "RO" + System.currentTimeMillis(),
            "Furnizor Candidat SRL",
            null,
            null,
            new ro.uaic.feaa.sdbis.supplychain.calitate.domain.Iban("RO98BTRL0000000000000002"),
            java.util.Set.of("TABLA-01"),
            null
        );
        // Stare Candidat -> Neeligibil
        furnizorRepo.add(f);

        String json = """
            {
              "numarComanda": "CMD-TEST-FAIL-%s",
              "furnizorId": "%s",
              "cumparatorId": "%s",
              "dataComanda": "2026-03-01",
              "dataLivrareCeruta": "2026-03-20",
              "depozit": "Depozit Central",
              "linii": [
                {
                  "materialCod": "TABLA-01",
                  "cantitate": 10.0,
                  "unitateMasura": "TO",
                  "pretUnitar": 100.0,
                  "moneda": "EUR"
                }
              ]
            }
            """.formatted(System.currentTimeMillis(), furnizorId.getValue(), UUID.randomUUID());

        mockMvc.perform(post("/api/comenzi")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("POST /api/comenzi - Invalid request body with missing fields returns 400 Bad Request (Jakarta Validation)")
    void testCreateOrderInvalidInputReturns400BadRequest() throws Exception {
        String invalidJson = """
            {
              "numarComanda": "",
              "depozit": "",
              "linii": []
            }
            """;

        mockMvc.perform(post("/api/comenzi")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Validation Failed"))
            .andExpect(jsonPath("$.fieldErrors").exists());
    }
}

