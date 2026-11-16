package ro.uaic.feaa.sdbis.supplychain.facturi.presentation;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.StripeWebhookProcessingService;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.dto.WebhookResponseDTO;

/**
 * Controller Webhook pentru receptionarea notificarilor Stripe (ADR-010, SEC-STRIPE-01).
 * Suporta atat procesare standard cat si decuplare asincrona pura prin Transactional Inbox (SLA < 50ms).
 */
@RestController
@RequestMapping("/api/webhooks")
public class StripeWebhookCtrl {

    private final StripeWebhookProcessingService webhookProcessingService;

    public StripeWebhookCtrl(StripeWebhookProcessingService webhookProcessingService) {
        this.webhookProcessingService = webhookProcessingService;
    }

    @PostMapping("/stripe")
    @Operation(summary = "Proceseaza webhook-ul Stripe cu validare criptografica obligatorie Stripe-Signature (FIX SEC-01, REST-02, SEC-STRIPE-01)")
    public ResponseEntity<WebhookResponseDTO> handleStripeWebhook(
            @RequestBody String rawPayload,
            @RequestHeader("Stripe-Signature") String sigHeader,
            @RequestHeader(value = "X-Processing-Mode", required = false) String processingMode) {
        var result = "async".equalsIgnoreCase(processingMode)
                ? webhookProcessingService.proceseazaStripeAsincron(rawPayload, sigHeader)
                : webhookProcessingService.proceseazaStripeStandard(rawPayload, sigHeader);
        return ResponseEntity.status(result.statusCode()).body(WebhookResponseDTO.fromResult(result));
    }

    @PostMapping("/stripe/async")
    @Operation(summary = "Receptie asincrona garantata a webhook-urilor Stripe cu persistenta in Inbox si raspuns < 50ms (SEC-STRIPE-01)")
    public ResponseEntity<WebhookResponseDTO> handleStripeWebhookAsync(
            @RequestBody String rawPayload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        var result = webhookProcessingService.proceseazaStripeAsincron(rawPayload, sigHeader);
        return ResponseEntity.status(result.statusCode()).body(WebhookResponseDTO.fromResult(result));
    }
}
