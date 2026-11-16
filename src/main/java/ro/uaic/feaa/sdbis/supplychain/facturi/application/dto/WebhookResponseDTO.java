package ro.uaic.feaa.sdbis.supplychain.facturi.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO puternic tipizat pentru raspunsurile endpoint-urilor de Webhook financiar (FIX REST-02).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record WebhookResponseDTO(
        String status,
        boolean received,
        String eventId,
        String eventType,
        String message,
        String error,
        String details,
        Instant timestamp
) implements Serializable {

    public static WebhookResponseDTO fromResult(WebhookProcessingResultDTO result) {
        Instant now = Instant.now();
        if (result.statusCode() == 200) {
            return new WebhookResponseDTO(
                    result.status() != null ? result.status() : "success",
                    true,
                    result.eventId(),
                    result.eventType(),
                    result.message(),
                    null,
                    null,
                    now
            );
        } else {
            return new WebhookResponseDTO(
                    "error",
                    false,
                    result.eventId(),
                    result.eventType(),
                    null,
                    result.message(),
                    result.details(),
                    now
            );
        }
    }
}
