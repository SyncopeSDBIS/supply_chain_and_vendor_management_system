package ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

/**
 * Request payload structurat pentru receptia unui lot (POST /api/comenzi/{id}/receptii).
 */
public record InregistreazaReceptieRequest(
    @NotNull(message = "ID-ul lotului este obligatoriu")
    UUID lotId,

    @Positive(message = "Cantitatea receptionata trebuie sa fie strict pozitiva")
    double cantitate,

    String unitate
) {}
