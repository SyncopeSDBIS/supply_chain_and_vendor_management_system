package ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import java.util.UUID;

/**
 * Request payload structurat pentru inregistrarea expedierii (POST /api/comenzi/{id}/expedieri).
 */
public record InregistreazaExpediereRequest(
    @NotBlank(message = "Numele transportatorului este obligatoriu")
    String transportator,

    @NotBlank(message = "Numarul AWB este obligatoriu")
    String awb,

    Set<UUID> loturiIds
) {}
