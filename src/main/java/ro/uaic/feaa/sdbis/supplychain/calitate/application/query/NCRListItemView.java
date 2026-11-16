package ro.uaic.feaa.sdbis.supplychain.calitate.application.query;

import ro.uaic.feaa.sdbis.supplychain.calitate.domain.StareNCR;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public record NCRListItemView(
        UUID idNcr,
        UUID inspectieId,
        UUID furnizorId,
        UUID lotId,
        String cauzaRadacina,
        String descriereActiune,
        String responsabil,
        LocalDate termen,
        StareNCR stareNcr
) implements Serializable {}

