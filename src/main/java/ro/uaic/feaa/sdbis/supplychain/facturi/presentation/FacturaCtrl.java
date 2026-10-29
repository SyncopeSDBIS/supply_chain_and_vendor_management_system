package ro.uaic.feaa.sdbis.supplychain.facturi.presentation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.FacturaApplicationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.command.*;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.query.FacturaQueryService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.LinieFactura;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.Suma;
import ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure.StripeProperties;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IJurnalAudit;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@RestController
@Validated
@RequestMapping("/api")
public class FacturaCtrl {

    private static final Logger log = LoggerFactory.getLogger(FacturaCtrl.class);

    private final FacturaApplicationService appService;
    private final FacturaQueryService queryService;
    private final StripeProperties stripeProperties;
    private final IJurnalAudit jurnalAudit;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FacturaCtrl(FacturaApplicationService appService,
                       FacturaQueryService queryService,
                       StripeProperties stripeProperties,
                       IJurnalAudit jurnalAudit) {
        this.appService = appService;
        this.queryService = queryService;
        this.stripeProperties = stripeProperties;
        this.jurnalAudit = jurnalAudit;
    }

    @PostMapping(value = "/facturi", consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> inregistreazaFacturaJson(@Valid @RequestBody InregistreazaFacturaRequest req) {
        return processInregistreazaFactura(req);
    }

    @PostMapping(value = "/facturi")
    public ResponseEntity<?> inregistreazaFacturaForm(@Valid @ModelAttribute InregistreazaFacturaRequest req) {
        return processInregistreazaFactura(req);
    }

    private ResponseEntity<?> processInregistreazaFactura(InregistreazaFacturaRequest req) {
        if (req.getSerie() == null || req.getNumar() == null || req.getDataEmitere() == null
                || req.getDataScadenta() == null || req.getComandaId() == null || req.getFurnizorId() == null) {
            return ResponseEntity.badRequest().body("Parametrii obligatorii lipsesc pentru inregistrarea facturii");
        }

        try {
            LinieFactura linie = new LinieFactura(
                CodMaterial.of(req.getMaterialCod()),
                BigDecimal.valueOf(req.getCantitate()),
                req.getUnitate(),
                Suma.of(req.getPretUnitar(), req.getMoneda()),
                BigDecimal.valueOf(0.19)
            );

            UUID id = appService.inregistreazaFactura(new InregistreazaFacturaCommand(
                req.getSerie(), req.getNumar(), LocalDate.parse(req.getDataEmitere()), LocalDate.parse(req.getDataScadenta()),
                IdComanda.of(req.getComandaId()), IdFurnizor.of(req.getFurnizorId()),
                Collections.singletonList(linie), Currency.getInstance(req.getMoneda())
            ));

            Map<String, Object> resp = new HashMap<>();
            resp.put("facturaId", id);
            resp.put("status", "Inregistrata");
            return new ResponseEntity<>(resp, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/facturi/{id}/contestare")
    public ResponseEntity<?> contestaFactura(@PathVariable UUID id, @RequestParam String motiv) {
        try {
            appService.contestaFactura(id, motiv);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/facturi/{id}/reconciliere")
    public ResponseEntity<?> reconcilieazaFactura(@PathVariable UUID id) {
        try {
            appService.reconcilieazaFactura(new ReconciliereCommand(id));
            Map<String, String> resp = new HashMap<>();
            resp.put("status", "SUCCESS");
            resp.put("message", "Reconcilierea a fost executata");
            return ResponseEntity.ok(resp);
        } catch (IllegalStateException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return new ResponseEntity<>(err, HttpStatus.CONFLICT);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/nir/{id}/contabilizare")
    public ResponseEntity<?> contabilizeazaNIR(@PathVariable UUID id) {
        try {
            appService.contabilizeazaNIR(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/nir/{id}/stornare")
    public ResponseEntity<?> storneazaNIR(@PathVariable UUID id, @RequestParam String motiv) {
        try {
            appService.storneazaNIR(id, motiv);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/facturi/{id}/ordine-plata", consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> creeazaOrdinPlataJson(
            @PathVariable UUID id,
            @Valid @RequestBody CreeazaOrdinPlataRequest req) {
        return processCreeazaOrdinPlata(id, req);
    }

    @PostMapping(value = "/facturi/{id}/ordine-plata")
    public ResponseEntity<?> creeazaOrdinPlataForm(
            @PathVariable UUID id,
            @Valid @ModelAttribute CreeazaOrdinPlataRequest req) {
        return processCreeazaOrdinPlata(id, req);
    }

    private ResponseEntity<?> processCreeazaOrdinPlata(UUID id, CreeazaOrdinPlataRequest req) {
        if (req.getCreatDe() == null || req.getSuma() <= 0) {
            return ResponseEntity.badRequest().body("Parametrii obligatorii lipsesc pentru crearea ordinului de plata");
        }

        try {
            UUID ordinId = appService.creeazaOrdinPlata(new CreeazaOrdinPlataCommand(id, Suma.of(req.getSuma(), req.getMoneda()), req.getCreatDe()));
            Map<String, Object> resp = new HashMap<>();
            resp.put("ordinId", ordinId);
            resp.put("facturaId", id);
            return new ResponseEntity<>(resp, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/ordine-plata/{id}/aprobare")
    public ResponseEntity<?> aprobaOrdin(@PathVariable UUID id, @RequestParam UUID aprobatorId) {
        try {
            appService.aprobaOrdin(id, aprobatorId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/ordine-plata/{id}/transmitere")
    public ResponseEntity<?> transmiteOrdin(@PathVariable UUID id, @RequestParam String iban) {
        try {
            appService.transmiteOrdin(new TransmiteOrdinCommand(id, iban));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = {"/ordine-plata/webhook-banca", "/plati/webhook-banca"})
    public ResponseEntity<?> webhookBanca(
            @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader,
            @RequestBody(required = false) String payload,
            @RequestParam(required = false) UUID ordinId,
            @RequestParam(required = false, defaultValue = "payment_intent.succeeded") String eventType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String motiv) {

        // 1. Mandatory Stripe-Signature verification (CRITICAL FIX #1)
        if (sigHeader == null || sigHeader.trim().isEmpty()) {
            log.warn("[Stripe Security Alert] Webhook call rejected: Missing mandatory 'Stripe-Signature' header.");
            jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_REJECTED", "Missing Stripe-Signature header");
            Map<String, String> err = new HashMap<>();
            err.put("error", "Lipseste antetul Stripe-Signature obligatoriu pentru validarea webhook-ului");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }

        if (payload == null || payload.trim().isEmpty()) {
            log.warn("[Stripe Security Alert] Webhook call rejected: Empty payload");
            jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_REJECTED", "Empty webhook payload");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Lipseste corpul webhook-ului"));
        }

        String secret = stripeProperties.getWebhookSecret();
        Event event;
        try {
            // Replay attack prevention: tolerance = 300 seconds
            event = Webhook.constructEvent(payload, sigHeader, secret, 300);
        } catch (SignatureVerificationException e) {
            log.warn("[Stripe Security Alert] Invalid signature on Stripe webhook: {}", e.getMessage());
            jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_INVALID_SIGNATURE", "Semnatura Stripe invalida: " + e.getMessage());
            Map<String, String> err = new HashMap<>();
            err.put("error", "Semnatura Stripe invalida");
            err.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        } catch (Exception e) {
            log.error("[Stripe Webhook Error] Failed to parse webhook event: {}", e.getMessage());
            jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_ERROR", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Eroare la procesarea webhook-ului: " + e.getMessage()));
        }

        // 2. Extract validated event data
        UUID targetOrdinId = ordinId;
        String type = event.getType();
        String reason = motiv != null ? motiv : "Tranzactie respinsa de banca / Stripe (Event " + event.getId() + ")";

        if (event.getDataObjectDeserializer() != null && event.getDataObjectDeserializer().getObject().isPresent()) {
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
            } catch (Exception ignored) {}
        }

        if (targetOrdinId == null) {
            log.warn("[Stripe Webhook] Verified event {} missing 'idOrdin' in metadata", event.getId());
            return ResponseEntity.badRequest().body(Map.of("error", "Lipseste ordinId din metadata webhook-ului Stripe"));
        }

        try {
            boolean isFailed = "payment_intent.payment_failed".equalsIgnoreCase(type)
                    || "failed".equalsIgnoreCase(status)
                    || "FAILED".equalsIgnoreCase(type)
                    || "RESPINS".equalsIgnoreCase(status);

            if (isFailed) {
                appService.respingeDeBanca(new RespingereBancaCommand(targetOrdinId, reason));
                jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_PROCESSED", "Respingere inregistrata pentru ordinul: " + targetOrdinId);
                Map<String, Object> resp = new HashMap<>();
                resp.put("ordinId", targetOrdinId);
                resp.put("status", "RespinsDeBanca");
                resp.put("motiv", reason);
                return ResponseEntity.ok(resp);
            } else {
                appService.inregistreazaExecutiePlata(new InregistreazaExecutiePlataCommand(targetOrdinId));
                jurnalAudit.inregistreazaAcces("STRIPE_WEBHOOK_PROCESSED", "Executie inregistrata pentru ordinul: " + targetOrdinId);
                Map<String, Object> resp = new HashMap<>();
                resp.put("ordinId", targetOrdinId);
                resp.put("status", "Executat");
                return ResponseEntity.ok(resp);
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body("Ordinul nu a fost gasit: " + targetOrdinId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/facturi/scadente")
    public ResponseEntity<?> obtineScadentar() {
        return ResponseEntity.ok(queryService.obtineScadentar());
    }
}
