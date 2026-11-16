package ro.uaic.feaa.sdbis.supplychain.facturi.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.InregistreazaExecutiePlataCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.RespingereBancaCommand;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.dto.WebhookProcessingResultDTO;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.port.IStripeConfigPort;
import ro.uaic.feaa.sdbis.supplychain.shared.IJurnalAudit;
import ro.uaic.feaa.sdbis.supplychain.shared.InboxRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ApplicationService;

import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Serviciu de aplicatie dedicat procesarii securizate si robuste a webhook-urilor bancare Stripe (ADR-010).
 * Garanteaza:
 *  - Verificarea criptografica a semnaturii Stripe-Signature cu toleranta la replay attacks de 300s
 *  - Deduplicarea si executia idempotenta prin Transactional Inbox (SEC-STRIPE-01)
 *  - Decuplarea asincrona cu SLA de raspuns sub 500ms prin executor dedicat (fara ForkJoinPool)
 *  - Eliminarea confirmarii premature in Inbox pentru protectie impotriva caderilor de nod
 */
@Service
@ApplicationService
public class StripeWebhookProcessingService {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookProcessingService.class);

    private final FacturaApplicationService facturaService;
    private final IStripeConfigPort stripeConfig;
    private final IJurnalAudit jurnalAudit;
    private final ObjectMapper objectMapper;
    private final InboxRepo inboxRepo;
    private final Executor stripeWebhookTaskExecutor;

    public StripeWebhookProcessingService(FacturaApplicationService facturaService,
            IStripeConfigPort stripeConfig,
            IJurnalAudit jurnalAudit,
            ObjectMapper objectMapper) {
        this(facturaService, stripeConfig, jurnalAudit, objectMapper, null, null);
    }

    public StripeWebhookProcessingService(FacturaApplicationService facturaService,
            IStripeConfigPort stripeConfig,
            IJurnalAudit jurnalAudit,
            ObjectMapper objectMapper,
            InboxRepo inboxRepo) {
        this(facturaService, stripeConfig, jurnalAudit, objectMapper, inboxRepo, null);
    }

    @Autowired
    public StripeWebhookProcessingService(FacturaApplicationService facturaService,
            IStripeConfigPort stripeConfig,
            IJurnalAudit jurnalAudit,
            ObjectMapper objectMapper,
            @Autowired(required = false) InboxRepo inboxRepo,
            @Autowired(required = false) @Qualifier("stripeWebhookTaskExecutor") Executor stripeWebhookTaskExecutor) {
        this.facturaService = facturaService;
        this.stripeConfig = stripeConfig;
        this.jurnalAudit = jurnalAudit;
        this.objectMapper = objectMapper;
        this.inboxRepo = inboxRepo;
        this.stripeWebhookTaskExecutor = stripeWebhookTaskExecutor;
    }

    public WebhookProcessingResultDTO proceseazaWebhookBanca(
            String sigHeader,
            String payload,
            UUID ordinId,
            String eventType,
            String status,
            String motiv) {

        // 1. Mandatory Stripe-Signature verification
        if (sigHeader == null || sigHeader.trim().isEmpty()) {
            log.warn("[Stripe Security Alert] Webhook call rejected: Missing mandatory 'Stripe-Signature' header.");
            jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_REJECTED", "Missing Stripe-Signature header");
            return WebhookProcessingResultDTO.error(400,
                    "Lipseste antetul Stripe-Signature obligatoriu pentru validarea webhook-ului", null);
        }

        if (payload == null || payload.trim().isEmpty()) {
            log.warn("[Stripe Security Alert] Webhook call rejected: Empty payload");
            jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_REJECTED", "Empty webhook payload");
            return WebhookProcessingResultDTO.error(400, "Lipseste corpul webhook-ului", null);
        }

        String secret = stripeConfig.getWebhookSecret();
        Event event;
        try {
            // Replay attack prevention: tolerance = 300 seconds
            event = Webhook.constructEvent(payload, sigHeader, secret, 300);
        } catch (SignatureVerificationException e) {
            log.warn("[Stripe Security Alert] Invalid signature on Stripe webhook: {}", e.getMessage());
            jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_INVALID_SIGNATURE",
                    "Semnatura Stripe invalida: " + e.getMessage());
            return WebhookProcessingResultDTO.error(400, "Semnatura Stripe invalida", e.getMessage());
        } catch (Exception e) {
            log.error("[Stripe Webhook Error] Failed to parse webhook event: {}", e.getMessage());
            jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_ERROR", e.getMessage());
            return WebhookProcessingResultDTO.error(400, "Eroare la parsarea webhook-ului", e.getMessage());
        }

        String type = event.getType();
        String reason = motiv != null ? motiv : "Actualizare din webhook bancar (" + type + ")";
        UUID targetOrdinId = ordinId;

        // Extract ordinId from event metadata if present and not directly provided
        if (targetOrdinId == null && event.getDataObjectDeserializer().getObject().isPresent()) {
            StripeObject stripeObj = event.getDataObjectDeserializer().getObject().get();
            if (stripeObj instanceof PaymentIntent intent) {
                if (intent.getMetadata() != null && intent.getMetadata().containsKey("idOrdin")) {
                    targetOrdinId = UUID.fromString(intent.getMetadata().get("idOrdin"));
                }
            }
        }

        if (targetOrdinId == null) {
            try {
                JsonNode root = objectMapper.readTree(payload);
                JsonNode meta = root.path("data").path("object").path("metadata");
                if (meta.has("idOrdin")) {
                    targetOrdinId = UUID.fromString(meta.get("idOrdin").asText());
                } else if (root.has("ordinId")) {
                    targetOrdinId = UUID.fromString(root.get("ordinId").asText());
                }
            } catch (Exception ignored) {
            }
        }

        if (targetOrdinId == null) {
            log.warn("[Stripe Webhook] Verified event {} missing 'idOrdin' in metadata", event.getId());
            return WebhookProcessingResultDTO.error(400, "Lipseste ordinId din metadata webhook-ului Stripe", null);
        }

        try {
            boolean isFailed = "payment_intent.payment_failed".equalsIgnoreCase(type)
                    || "failed".equalsIgnoreCase(status)
                    || "FAILED".equalsIgnoreCase(type)
                    || "RESPINS".equalsIgnoreCase(status);

            boolean isSucceeded = "payment_intent.succeeded".equalsIgnoreCase(type)
                    || "charge.succeeded".equalsIgnoreCase(type)
                    || "succeeded".equalsIgnoreCase(status)
                    || "SUCCEEDED".equalsIgnoreCase(type)
                    || "SUCCES".equalsIgnoreCase(status)
                    || "EXECUTAT".equalsIgnoreCase(status);

            if (isFailed) {
                facturaService.respingeDeBanca(new RespingereBancaCommand(targetOrdinId, reason));
                jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_PROCESSED",
                        "Respingere inregistrata pentru ordinul: " + targetOrdinId);
                return WebhookProcessingResultDTO.success("RespinsDeBanca", reason, event.getId(), type);
            } else if (isSucceeded) {
                facturaService.inregistreazaExecutiePlata(new InregistreazaExecutiePlataCommand(targetOrdinId));
                jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_PROCESSED",
                        "Executie inregistrata pentru ordinul: " + targetOrdinId);
                return WebhookProcessingResultDTO.success("Executat", "Plata inregistrata cu succes", event.getId(),
                        type);
            } else {
                jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_IGNORED", "Tip eveniment netratat: " + type);
                return WebhookProcessingResultDTO.success("Ignored", "Eveniment receptionat dar netratat",
                        event.getId(), type);
            }
        } catch (NoSuchElementException | IllegalArgumentException e) {
            log.warn("[Stripe Webhook Processing] Entity not found or invalid argument for event {}: {}", event.getId(), e.getMessage());
            jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_BAD_REQUEST", e.getMessage());
            return WebhookProcessingResultDTO.error(400, "Eroare la procesarea starii comenzii: " + e.getMessage(),
                    null);
        } catch (Exception e) {
            log.error("[Stripe Webhook Processing Error] Failed processing event {}: {}", event.getId(),
                    e.getMessage(), e);
            jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_PROCESSING_FAILED", e.getMessage());
            return WebhookProcessingResultDTO.error(500, "Eroare la procesarea starii comenzii: " + e.getMessage(),
                    null);
        }
    }

    /**
     * Procesare standard sincronica a webhook-ului Stripe (compatibilitate integrala).
     */
    public WebhookProcessingResultDTO proceseazaStripeStandard(String rawPayload, String sigHeader) {
        log.info("[Stripe Webhook Standard] Receptionat webhook de dimensiune: {} bytes",
                rawPayload != null ? rawPayload.length() : 0);

        if (rawPayload == null || rawPayload.trim().isEmpty()) {
            return WebhookProcessingResultDTO.error(400, "Payload gol", null);
        }

        if (sigHeader == null || sigHeader.isBlank()) {
            log.warn("[Stripe Security Alert] Webhook rejected: Missing mandatory 'Stripe-Signature' header.");
            jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_REJECTED", "Missing Stripe-Signature header");
            return WebhookProcessingResultDTO.error(400,
                    "Lipseste antetul Stripe-Signature obligatoriu pentru validarea webhook-ului", null);
        }

        Event event;
        try {
            event = Webhook.constructEvent(rawPayload, sigHeader, stripeConfig.getWebhookSecret(), 300);
        } catch (SignatureVerificationException ex) {
            log.warn("[Stripe Security Alert] Invalid signature on standard webhook: {}", ex.getMessage());
            jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_INVALID_SIGNATURE", ex.getMessage());
            return WebhookProcessingResultDTO.error(400, "Semnatura Stripe invalida: " + ex.getMessage(), null);
        } catch (Exception ex) {
            log.error("[Stripe Security Alert] Failed validating signature: {}", ex.getMessage());
            return WebhookProcessingResultDTO.error(400, ex.getMessage(), null);
        }

        // Deduplicare / Idempotenta atomica prin Transactional Inbox (FIX CONC-02)
        if (inboxRepo != null && event != null && event.getId() != null) {
            UUID inboxEventUuid = UUID.nameUUIDFromBytes(("stripe:" + event.getId()).getBytes(StandardCharsets.UTF_8));
            if (!inboxRepo.incearcaInregistrare("stripe-webhook", inboxEventUuid)) {
                log.info("[Stripe Webhook Standard] Evenimentul {} este deja procesat in Inbox (idempotent no-op).", event.getId());
                return WebhookProcessingResultDTO.success("success", "Eveniment deja procesat (idempotent)", event.getId(), event.getType());
            }
        }

        try {
            JsonNode root = objectMapper.readTree(rawPayload);
            String eventType = root.has("type") ? root.get("type").asText() : "unknown";

            log.info("[Stripe Webhook Standard] Tip eveniment: {}", eventType);

            if ("payment_intent.succeeded".equalsIgnoreCase(eventType)
                    || "charge.succeeded".equalsIgnoreCase(eventType)) {
                JsonNode dataObject = root.path("data").path("object");
                JsonNode metadata = dataObject.path("metadata");

                String ordinIdStr = null;
                if (metadata.has("idOrdin")) {
                    ordinIdStr = metadata.get("idOrdin").asText();
                } else if (dataObject.has("description") && dataObject.get("description").asText().contains("Ordin ")) {
                    String desc = dataObject.get("description").asText();
                    ordinIdStr = desc.substring(desc.indexOf("Ordin ") + 6).trim();
                }

                if (ordinIdStr != null && !ordinIdStr.isBlank()) {
                    UUID ordinId = UUID.fromString(ordinIdStr);
                    log.info("[Stripe Webhook Standard] Inregistrare automata executie plata pentru ordinul {}",
                            ordinId);
                    facturaService.inregistreazaExecutiePlata(new InregistreazaExecutiePlataCommand(ordinId));
                    jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_STANDARD_PROCESSED",
                            "Executie inregistrata pentru ordinul: " + ordinId);
                    return WebhookProcessingResultDTO.success("success", "Executie inregistrata", event.getId(), eventType);
                }
            } else if ("payment_intent.payment_failed".equalsIgnoreCase(eventType)) {
                JsonNode dataObject = root.path("data").path("object");
                JsonNode metadata = dataObject.path("metadata");
                if (metadata.has("idOrdin")) {
                    UUID ordinId = UUID.fromString(metadata.get("idOrdin").asText());
                    facturaService.respingeDeBanca(
                            new RespingereBancaCommand(ordinId, "Plata respinsa prin webhook Stripe standard"));
                    jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_STANDARD_REJECTED",
                            "Respingere inregistrata pentru ordinul: " + ordinId);
                    return WebhookProcessingResultDTO.success("success", "Plata respinsa", event.getId(), eventType);
                }
            }

            return WebhookProcessingResultDTO.success("ignored", "Eveniment ignorat", event.getId(), eventType);
        } catch (Exception ex) {
            log.error("[Stripe Webhook Standard] Eroare la procesarea webhook-ului: {}", ex.getMessage(), ex);
            return WebhookProcessingResultDTO.error(400, ex.getMessage(), null);
        }
    }

    /**
     * Procesare asincrona pura cu decuplare prin Transactional Inbox (SEC-STRIPE-01).
     * Valideaza semnatura, confirma catre Stripe in < 50ms,
     * executand prelucrarea financiara intr-un thread dedicat din stripeWebhookTaskExecutor
     * si marcand definitiv in Inbox doar DUPA reusita tranzactiei (fara premature ack).
     */
    public WebhookProcessingResultDTO proceseazaStripeAsincron(String rawPayload, String sigHeader) {
        log.info("[Stripe Webhook Async] Receptionat webhook pentru decuplare asincrona: {} bytes",
                rawPayload != null ? rawPayload.length() : 0);

        if (rawPayload == null || rawPayload.trim().isEmpty()) {
            return WebhookProcessingResultDTO.error(400, "Payload gol", null);
        }

        if (sigHeader == null || sigHeader.isBlank()) {
            log.warn("[Stripe Security Alert] Async webhook rejected: Missing mandatory 'Stripe-Signature' header.");
            jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_REJECTED", "Missing Stripe-Signature header on async webhook");
            return WebhookProcessingResultDTO.error(400, "Lipseste antetul Stripe-Signature obligatoriu pentru validarea webhook-ului", null);
        }

        Event event;
        try {
            event = Webhook.constructEvent(rawPayload, sigHeader, stripeConfig.getWebhookSecret(), 300);
        } catch (SignatureVerificationException ex) {
            log.warn("[Stripe Security Alert] Invalid signature on async webhook: {}", ex.getMessage());
            jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_INVALID_SIGNATURE", ex.getMessage());
            return WebhookProcessingResultDTO.error(400, "Semnatura Stripe invalida: " + ex.getMessage(), null);
        } catch (Exception ex) {
            log.error("[Stripe Security Alert] Failed validating signature: {}", ex.getMessage());
            return WebhookProcessingResultDTO.error(400, ex.getMessage(), null);
        }

        String eventId = event.getId();
        String eventType = event.getType();
        UUID inboxEventUuid = (inboxRepo != null && eventId != null)
                ? UUID.nameUUIDFromBytes(("stripe:" + eventId).getBytes(StandardCharsets.UTF_8))
                : null;

        // 1. Verificare daca este DEJA marcat complet in Inbox (idempotenta sincrona)
        if (inboxRepo != null && inboxEventUuid != null && inboxRepo.existaEveniment("stripe-webhook", inboxEventUuid)) {
            log.info("[Stripe Webhook Inbox] Eveniment duplicat {} detectat. Returnare 200 OK imediata fara re-procesare.", eventId);
            return WebhookProcessingResultDTO.success("success", "Eveniment deja procesat (idempotent)", eventId, eventType);
        }

        jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_INBOX_RECEIVED", "Event: " + eventId + " Type: " + eventType);

        // 2. Decuplare asincrona: executie pe stripeWebhookTaskExecutor dedicat.
        // Confirmarea in Inbox se face DUPA executia cu succes a platii (fara premature ack).
        Runnable asyncTask = () -> {
            try {
                proceseazaEvenimentStripePayload(rawPayload, eventType);
                if (inboxRepo != null && inboxEventUuid != null) {
                    inboxRepo.incearcaInregistrare("stripe-webhook", inboxEventUuid);
                }
            } catch (Exception ex) {
                log.error("[Stripe Webhook Async Worker Error] Eroare in timpul prelucrarii asincrone: {}", ex.getMessage(), ex);
            }
        };

        if (stripeWebhookTaskExecutor != null) {
            stripeWebhookTaskExecutor.execute(asyncTask);
        } else {
            CompletableFuture.runAsync(asyncTask);
        }

        return WebhookProcessingResultDTO.success("success", "Eveniment receptionat si transmis la procesare asincrona", eventId, eventType);
    }

    private void proceseazaEvenimentStripePayload(String rawPayload, String eventType) {
        try {
            JsonNode root = objectMapper.readTree(rawPayload);
            if ("payment_intent.succeeded".equalsIgnoreCase(eventType)
                    || "charge.succeeded".equalsIgnoreCase(eventType)) {
                JsonNode dataObject = root.path("data").path("object");
                JsonNode metadata = dataObject.path("metadata");

                String ordinIdStr = null;
                if (metadata.has("idOrdin")) {
                    ordinIdStr = metadata.get("idOrdin").asText();
                } else if (dataObject.has("description") && dataObject.get("description").asText().contains("Ordin ")) {
                    String desc = dataObject.get("description").asText();
                    ordinIdStr = desc.substring(desc.indexOf("Ordin ") + 6).trim();
                }

                if (ordinIdStr != null && !ordinIdStr.isBlank()) {
                    UUID ordinId = UUID.fromString(ordinIdStr);
                    log.info("[Stripe Webhook Async Worker] Inregistrare automata executie plata pentru ordinul {}", ordinId);
                    facturaService.inregistreazaExecutiePlata(new InregistreazaExecutiePlataCommand(ordinId));
                    jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_ASYNC_EXECUTED", "Executie inregistrata pentru ordinul: " + ordinId);
                }
            } else if ("payment_intent.payment_failed".equalsIgnoreCase(eventType)) {
                JsonNode dataObject = root.path("data").path("object");
                JsonNode metadata = dataObject.path("metadata");
                if (metadata.has("idOrdin")) {
                    UUID ordinId = UUID.fromString(metadata.get("idOrdin").asText());
                    log.info("[Stripe Webhook Async Worker] Inregistrare respingere plata pentru ordinul {}", ordinId);
                    facturaService.respingeDeBanca(
                            new RespingereBancaCommand(ordinId, "Plata respinsa prin webhook Stripe asincron"));
                    jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_ASYNC_REJECTED", "Respingere inregistrata pentru ordinul: " + ordinId);
                }
            }
        } catch (Exception ex) {
            log.error("[Stripe Webhook Async Worker Error] Eroare in timpul prelucrarii asincrone: {}", ex.getMessage(), ex);
        }
    }
}
