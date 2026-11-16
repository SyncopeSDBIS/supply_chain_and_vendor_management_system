package ro.uaic.feaa.sdbis.supplychain.comenzi.presentation;

import java.io.Serializable;
import java.util.UUID;

public record LotPlanificatResponse(
        UUID lotId,
        String codLot
) implements Serializable {}
