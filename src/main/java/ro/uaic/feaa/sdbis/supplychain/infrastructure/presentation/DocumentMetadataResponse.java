package ro.uaic.feaa.sdbis.supplychain.infrastructure.presentation;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record DocumentMetadataResponse(
        UUID id,
        String tipDocument,
        UUID referintaId,
        String numeFisier,
        String tipContinut,
        long dimensiuneBytes,
        Instant dataIncarcare
) implements Serializable {}
