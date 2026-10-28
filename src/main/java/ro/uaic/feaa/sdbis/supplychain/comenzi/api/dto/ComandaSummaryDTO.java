package ro.uaic.feaa.sdbis.supplychain.comenzi.api.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Public Contract DTO exposed by the Comenzi module for inter-module integration (e.g. 3-Way Matching).
 */
public record ComandaSummaryDTO(
        UUID idComanda,
        String numarComanda,
        UUID furnizorId,
        String stareComanda,
        LocalDate dataLivrareEstimata,
        List<LinieComandaDTO> linii
) implements Serializable {}
