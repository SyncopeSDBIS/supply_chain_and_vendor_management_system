package ro.uaic.feaa.sdbis.supplychain.comenzi.api.command;

import ro.uaic.feaa.sdbis.supplychain.comenzi.api.dto.LinieComandaDTO;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Public Command for creating a purchase order via the module's public API.
 */
public record CreeazaComandaPublicCommand(
        UUID furnizorId,
        UUID gestionarId,
        LocalDate dataLivrareEstimata,
        String depozitDestinatie,
        List<LinieComandaDTO> linii
) implements Serializable {}
