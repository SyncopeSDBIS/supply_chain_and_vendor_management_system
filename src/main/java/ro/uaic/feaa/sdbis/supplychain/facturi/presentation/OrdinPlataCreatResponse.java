package ro.uaic.feaa.sdbis.supplychain.facturi.presentation;

import java.io.Serializable;
import java.util.UUID;

public record OrdinPlataCreatResponse(
        UUID ordinId,
        UUID facturaId,
        String status
) implements Serializable {

    public OrdinPlataCreatResponse(UUID ordinId, UUID facturaId) {
        this(ordinId, facturaId, "Creat");
    }
}
