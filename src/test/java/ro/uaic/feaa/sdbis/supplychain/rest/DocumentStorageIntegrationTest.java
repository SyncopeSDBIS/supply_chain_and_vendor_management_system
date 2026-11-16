package ro.uaic.feaa.sdbis.supplychain.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.test.context.TestPropertySource(properties = {
    "security.allow-anonymous-fallback=true",
    "security.mock-headers.enabled=true"
})
@DisplayName("DMS - Document Storage & Attachment Tests")
public class DocumentStorageIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("DMS: Incarcare factura scanata PDF si descarcare ulterioara a fisierului")
    void testUploadAndDownloadDocument() throws Exception {
        UUID facturaId = UUID.randomUUID();
        byte[] continutFisier = "%PDF-1.4 Mock Invoice Content For Testing".getBytes();
        MockMultipartFile fisier = new MockMultipartFile(
                "fisier",
                "factura_furnizor.pdf",
                "application/pdf",
                continutFisier
        );

        // 1. Incarcare document
        MvcResult uploadResult = mockMvc.perform(multipart("/api/documente/incarca")
                        .file(fisier)
                        .param("tipDocument", "FACTURA_SCANATA")
                        .param("referintaId", facturaId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.numeFisier").value("factura_furnizor.pdf"))
                .andReturn();

        JsonNode responseNode = objectMapper.readTree(uploadResult.getResponse().getContentAsString());
        String documentId = responseNode.get("id").asText();

        // 2. Cautare documente dupa referintaId
        mockMvc.perform(get("/api/documente/referinta/" + facturaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(documentId))
                .andExpect(jsonPath("$[0].tipDocument").value("FACTURA_SCANATA"));

        // 3. Descarcare continut binar
        MvcResult downloadResult = mockMvc.perform(get("/api/documente/" + documentId + "/descarca"))
                .andExpect(status().isOk())
                .andReturn();

        assertArrayEquals(continutFisier, downloadResult.getResponse().getContentAsByteArray());
    }
}

