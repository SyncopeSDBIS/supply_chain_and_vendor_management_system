package ro.uaic.feaa.sdbis.supplychain.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.port.IStripeConfigPort;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaFurnizor;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.FacturaRepo;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.LinieFactura;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.OrdinPlata;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.OrdinPlataRepo;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.Suma;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.InboxRepo;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("SEC-STRIPE-01: Stripe Webhook Asynchronous Decoupling & Idempotent Inbox Integration Tests")
public class StripeWebhookAsyncDecouplingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IStripeConfigPort stripeConfigPort;

    @Autowired
    private InboxRepo inboxRepo;

    @Autowired
    private FacturaRepo facturaRepo;

    @Autowired
    private OrdinPlataRepo ordinRepo;

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
    @DisplayName("SEC-STRIPE-01: Endpoint-ul asincron confirma in < 500ms, salveaza in Inbox si este strict idempotent")
    void testAsyncDecoupledStripeWebhookProcessing() throws Exception {
        // 1. Setup ordin de plata real
        IdComanda idCmd = IdComanda.random();
        IdFurnizor idFurnizor = IdFurnizor.random();
        FacturaFurnizor factura = new FacturaFurnizor(
                UUID.randomUUID(), "FC-STRIPE", "123", LocalDate.now(), LocalDate.now().plusDays(30),
                idCmd, idFurnizor,
                List.of(new LinieFactura(CodMaterial.of("M1"), BigDecimal.valueOf(10), "BUC", Suma.of(100.0, "EUR"), BigDecimal.valueOf(0.19))),
                Currency.getInstance("EUR")
        );
        factura.trimiteLaVerificare();
        factura.aproba();
        facturaRepo.add(factura);

        OrdinPlata ordin = factura.creeazaOrdinPlata(Suma.of(1190.0, "EUR"), UUID.randomUUID());
        ordinRepo.add(ordin);

        String eventId = "evt_async_test_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String payload = """
            {
                "id": "%s",
                "object": "event",
                "type": "payment_intent.succeeded",
                "data": {
                    "object": {
                        "id": "pi_async_12345",
                        "amount": 119000,
                        "currency": "eur",
                        "status": "succeeded",
                        "metadata": {
                            "idOrdin": "%s"
                        }
                    }
                }
            }
            """.formatted(eventId, ordin.getIdOrdin());

        String signature = generateSignature(payload);

        // 2. Apel asincron decuplat - masurare timp de raspuns
        long startMs = System.currentTimeMillis();
        mockMvc.perform(post("/api/webhooks/stripe/async")
                        .header("Stripe-Signature", signature)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.received").value(true))
                .andExpect(jsonPath("$.eventId").value(eventId));
        long latencyMs = System.currentTimeMillis() - startMs;

        assertTrue(latencyMs < 500, "Raspunsul webhook-ului asincron (" + latencyMs + " ms) trebuie sa fie ultrarapid (< 500 ms SLA).");

        // 3. Verificare ca evenimentul a fost stocat in Transactional Inbox
        UUID inboxUuid = UUID.nameUUIDFromBytes(("stripe:" + eventId).getBytes(StandardCharsets.UTF_8));
        long deadline = System.currentTimeMillis() + 3000;
        while (!inboxRepo.existaEveniment("stripe-webhook", inboxUuid) && System.currentTimeMillis() < deadline) {
            Thread.sleep(25);
        }
        assertTrue(inboxRepo.existaEveniment("stripe-webhook", inboxUuid),
                "Evenimentul Stripe trebuie sa fie persistat in InboxRepo pentru garantie de idempotenta.");

        // 4. Test Deduplicare / Idempotenta: Trimitere duplicat cu acelasi eventId
        mockMvc.perform(post("/api/webhooks/stripe/async")
                        .header("Stripe-Signature", signature)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.received").value(true));
    }
}
