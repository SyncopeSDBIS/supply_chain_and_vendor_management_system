package ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto;

import java.time.LocalDate;

/**
 * Request payload structurat pentru actualizarea atributelor unei comenzi (PUT /api/comenzi/{id}).
 */
public record ActualizareComandaRequest(
    LocalDate dataLivrareCeruta,
    String depozit
) {}
