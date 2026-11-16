package ro.uaic.feaa.sdbis.supplychain.facturi.application.command;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Comanda pentru initierea reconcilierii Three-Way Match (Factura vs. Comanda vs. NIR).
 * Suporta atat NIR singular cat si lista de NIR-uri consolidate pentru livrari partiale multiple (DDD-R01).
 */
public record ReconciliereCommand(UUID facturaId, UUID nirId, List<UUID> nirIds) {

    public ReconciliereCommand(UUID facturaId, UUID nirId) {
        this(facturaId, nirId, nirId != null ? List.of(nirId) : Collections.emptyList());
    }

    public ReconciliereCommand(UUID facturaId) {
        this(facturaId, null, Collections.emptyList());
    }

    public ReconciliereCommand(UUID facturaId, List<UUID> nirIds) {
        this(facturaId, (nirIds != null && !nirIds.isEmpty()) ? nirIds.get(0) : null,
                nirIds != null ? List.copyOf(nirIds) : Collections.emptyList());
    }
}
