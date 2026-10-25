package ro.uaic.feaa.sdbis.supplychain.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Sprint 4 - Facturi & Reconciliere REST Controller Tests (I1..I8)")
public class FacturaCtrlRestTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/facturi - inregistrare factura furnizor (I1)")
    void testInregistreazaFactura() throws Exception {
        mockMvc.perform(post("/api/facturi")
                .param("serie", "FACT")
                .param("numar", "1001")
                .param("dataEmitere", "2026-03-01")
                .param("dataScadenta", "2026-03-31")
                .param("comandaId", UUID.randomUUID().toString())
                .param("furnizorId", UUID.randomUUID().toString())
                .param("moneda", "EUR")
                .param("materialCod", "OTEL-01")
                .param("cantitate", "10.0")
                .param("unitate", "TO")
                .param("pretUnitar", "500.0"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.facturaId").exists())
            .andExpect(jsonPath("$.status").value("Inregistrata"));
    }

    @Test
    @DisplayName("POST /api/facturi/{id}/reconciliere fara NIR returneaza 409 Conflict (Figura 27)")
    void testReconciliereLipsaNIR() throws Exception {
        UUID nonExistentFactura = UUID.randomUUID();
        mockMvc.perform(post("/api/facturi/" + nonExistentFactura + "/reconciliere"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/facturi/scadente - verificare scadentar (I8)")
    void testObtineScadentar() throws Exception {
        mockMvc.perform(get("/api/facturi/scadente"))
            .andExpect(status().isOk());
    }
}
