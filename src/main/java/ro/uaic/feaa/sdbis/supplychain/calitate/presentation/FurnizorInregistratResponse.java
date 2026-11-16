package ro.uaic.feaa.sdbis.supplychain.calitate.presentation;

import java.io.Serializable;
import java.util.UUID;

public record FurnizorInregistratResponse(
        UUID furnizorId,
        String codFiscal,
        String status
) implements Serializable {}
