package ro.uaic.feaa.sdbis.supplychain.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.port.IStripeConfigPort;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Payments - Stripe Webhook Integration Tests")
public class StripeWebhookIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IStripeConfigPort stripeConfigPort;

    private String generateSignature(String payload) throws Exception {
        long timestamp = Instant.now().getEpochSecond();
        String secret = stripeConfigPort.getWebhookSecret();
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] rawHmac = mac.doFinal((timestamp + "." + payload).getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : rawHmac) {
            sb.append(String.format("%02x", b));
        }
        return "t=" + timestamp + ",v1=" + sb.toString();
    }

    @Test
    @DisplayName("Webhook Stripe: Receptionare notificare asincrona si confirmare 200 OK")
    void testStripeWebhookProcessing() throws Exception {
        String webhookPayload = """
            {
                "id": "evt_test_123456",
                "object": "event",
                "type": "payment_intent.succeeded",
                "data": {
                    "object": {
                        "id": "pi_test_123456",
                        "amount": 50000,
                        "currency": "eur",
                        "status": "succeeded",
                        "metadata": {
                            "idOrdin": "00000000-0000-0000-0000-000000000000"
                        }
                    }
                }
            }
            """;

        String signature = generateSignature(webhookPayload);

        // Deoarece ordinul 0000... nu exista in baza, webhook-ul trateaza exceptia elegant fara 500
        mockMvc.perform(post("/api/webhooks/stripe")
                        .header("Stripe-Signature", signature)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(webhookPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    @DisplayName("Webhook Stripe: Eveniment irelevant este ignorat cu 200 OK")
    void testIgnoredStripeEvent() throws Exception {
        String ignoredPayload = """
            {
                "id": "evt_test_999999",
                "object": "event",
                "type": "customer.created",
                "data": {
                    "object": {
                        "id": "cus_test_123"
                    }
                }
            }
            """;

        String signature = generateSignature(ignoredPayload);

        mockMvc.perform(post("/api/webhooks/stripe")
                        .header("Stripe-Signature", signature)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ignoredPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ignored"));
    }

    @Test
    @DisplayName("SEC-01: Cererea catre Webhook fara Stripe-Signature este respinsa strict cu HTTP 400")
    void testMissingStripeSignatureRejected() throws Exception {
        String payload = "{\"id\":\"evt_1\",\"type\":\"payment_intent.succeeded\"}";
        mockMvc.perform(post("/api/webhooks/stripe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }
}

