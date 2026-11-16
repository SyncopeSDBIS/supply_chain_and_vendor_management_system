package ro.uaic.feaa.sdbis.supplychain.facturi.application.dto;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Rezultat tipizat al procesarii unui eveniment Webhook Stripe (FIX ARCH-01).
 * Pur de aplicatie - nu depinde de Spring MVC / ResponseEntity.
 * Respecta conventia de denumire ArchUnit *DTO.
 */
public record WebhookProcessingResultDTO(
        int statusCode,
        String status,
        String message,
        String details,
        String eventId,
        String eventType
) {
    public static WebhookProcessingResultDTO success(String status, String message, String eventId, String eventType) {
        return new WebhookProcessingResultDTO(200, status, message, null, eventId, eventType);
    }

    public static WebhookProcessingResultDTO error(int statusCode, String message, String details) {
        return new WebhookProcessingResultDTO(statusCode, "error", message, details, null, null);
    }

    public Map<String, Object> toResponseMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        if (statusCode == 200) {
            map.put("status", status != null ? status : "success");
            map.put("received", true);
            if (eventId != null) map.put("eventId", eventId);
            if (eventType != null) map.put("eventType", eventType);
            if (message != null) map.put("message", message);
        } else {
            map.put("status", "error");
            map.put("error", message);
            if (details != null) map.put("details", details);
        }
        return map;
    }
}
