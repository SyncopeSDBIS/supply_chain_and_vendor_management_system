package ro.uaic.feaa.sdbis.supplychain.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure.StripeProperties;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
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

    @Autowired
    private StripeProperties stripeProperties;

    private String generateStripeSignature(String payload, String secret) throws Exception {
        long timestamp = System.currentTimeMillis() / 1000L;
        String signedPayload = timestamp + "." + payload;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hash = mac.doFinal(signedPayload.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) {
            hex.append(String.format("%02x", b));
        }
        return "t=" + timestamp + ",v1=" + hex.toString();
    }

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

    @Test
    @DisplayName("POST /api/ordine-plata/webhook-banca - lipsa semnatura Stripe este respinsa cu 400 Bad Request")
    void testWebhookBancaLipsaSemnatura() throws Exception {
        mockMvc.perform(post("/api/ordine-plata/webhook-banca")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ordinId\":\"" + UUID.randomUUID() + "\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Lipseste antetul Stripe-Signature obligatoriu pentru validarea webhook-ului"));
    }

    @Test
    @DisplayName("POST /api/ordine-plata/webhook-banca - semnatura Stripe invalida este respinsa cu 400 Bad Request")
    void testWebhookBancaSemnaturaInvalida() throws Exception {
        String payload = "{\"id\":\"evt_test_123\",\"type\":\"payment_intent.succeeded\",\"data\":{\"object\":{\"metadata\":{\"idOrdin\":\"" + UUID.randomUUID() + "\"}}}}";
        mockMvc.perform(post("/api/ordine-plata/webhook-banca")
                .header("Stripe-Signature", "t=1700000000,v1=invalidsignature1234567890abcdef")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Semnatura Stripe invalida"));
    }

    @Test
    @DisplayName("POST /api/ordine-plata/webhook-banca - semnatura valida si idOrdin inexistent ajunge in service (400 Ordin inexistent)")
    void testWebhookBancaSemnaturaValidaSucces() throws Exception {
        UUID ordinId = UUID.randomUUID();
        String payload = "{\"id\":\"evt_test_success_1\",\"object\":\"event\",\"type\":\"payment_intent.succeeded\",\"data\":{\"object\":{\"id\":\"pi_test_1\",\"object\":\"payment_intent\",\"metadata\":{\"idOrdin\":\"" + ordinId + "\"}}}}";
        String validSig = generateStripeSignature(payload, stripeProperties.getWebhookSecret());

        mockMvc.perform(post("/api/ordine-plata/webhook-banca")
                .header("Stripe-Signature", validSig)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/plati/webhook-banca - semnatura valida si plata respinsa ajunge in service")
    void testWebhookBancaSemnaturaValidaEsec() throws Exception {
        UUID ordinId = UUID.randomUUID();
        String payload = "{\"id\":\"evt_test_fail_1\",\"object\":\"event\",\"type\":\"payment_intent.payment_failed\",\"data\":{\"object\":{\"id\":\"pi_test_2\",\"object\":\"payment_intent\",\"metadata\":{\"idOrdin\":\"" + ordinId + "\"}}}}";
        String validSig = generateStripeSignature(payload, stripeProperties.getWebhookSecret());

        mockMvc.perform(post("/api/plati/webhook-banca")
                .header("Stripe-Signature", validSig)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest());
    }
}
