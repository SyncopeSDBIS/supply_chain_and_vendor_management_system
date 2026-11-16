package ro.uaic.feaa.sdbis.supplychain.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@org.springframework.test.context.TestPropertySource(properties = {
    "security.allow-anonymous-fallback=true",
    "security.mock-headers.enabled=true"
})
@DisplayName("Sprint 4 - Calitate REST Controller Tests (Q1..Q9)")
public class CalitateCtrlRestTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/furnizori - inregistrare furnizor candidat")
    void testInregistreazaFurnizor() throws Exception {
        mockMvc.perform(post("/api/furnizori")
                .param("codFiscal", "RO" + System.currentTimeMillis())
                .param("denumire", "Furnizor Nou SRL")
                .param("iban", "RO98AAAA1234567890123456")
                .param("email", "office@furnizornou.ro"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.furnizorId").exists())
            .andExpect(jsonPath("$.status").value("Candidat"));
    }

    @Test
    @DisplayName("POST /api/inspectori - creare inspector calitate")
    void testCreeazaInspector() throws Exception {
        mockMvc.perform(post("/api/inspectori")
                .param("firstName", "Ion")
                .param("lastName", "Popescu"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.inspectorId").exists());
    }

    @Test
    @DisplayName("POST /api/furnizori - validare esueaza cand codul fiscal lipseste")
    void testInregistreazaFurnizorValidareEsueaza() throws Exception {
        mockMvc.perform(post("/api/furnizori")
                .param("codFiscal", "")
                .param("denumire", "Furnizor Fara CIF"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/furnizori/rapoarte/monitorizare - monitorizare furnizori (Q9)")
    void testMonitorizareFurnizori() throws Exception {
        mockMvc.perform(get("/api/furnizori/rapoarte/monitorizare"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/furnizori - listare paginata a furnizorilor")
    void testListeazaFurnizori() throws Exception {
        mockMvc.perform(get("/api/furnizori?page=0&size=10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/inspectori - listare inspectori calitate")
    void testListeazaInspectori() throws Exception {
        mockMvc.perform(get("/api/inspectori"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/inspectii - listare inspectii calitate")
    void testListeazaInspectii() throws Exception {
        mockMvc.perform(get("/api/inspectii"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/ncr - listare rapoarte neconformitate")
    void testListeazaNCR() throws Exception {
        mockMvc.perform(get("/api/ncr"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }
}
