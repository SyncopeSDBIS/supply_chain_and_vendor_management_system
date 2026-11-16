package ro.uaic.feaa.sdbis.supplychain.comenzi.presentation;

import java.io.Serializable;
import java.util.UUID;

public record ComandaCreatedResponse(
        UUID comandaId,
        String numarComanda,
        String status
) implements Serializable {}
