package ro.uaic.feaa.sdbis.supplychain.facturi.presentation;

import java.io.Serializable;
import java.util.UUID;

public record FacturaInregistrataResponse(
        UUID facturaId,
        String status
) implements Serializable {}
