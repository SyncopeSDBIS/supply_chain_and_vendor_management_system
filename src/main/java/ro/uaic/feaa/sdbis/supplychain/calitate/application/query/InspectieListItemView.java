package ro.uaic.feaa.sdbis.supplychain.calitate.application.query;

import ro.uaic.feaa.sdbis.supplychain.calitate.domain.StareInspectie;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Verdict;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record InspectieListItemView(
        UUID idInspectie,
        UUID lotId,
        UUID comandaId,
        UUID furnizorId,
        String codMaterial,
        LocalDate dataInceput,
        LocalDate dataFinalizare,
        UUID inspectorId,
        StareInspectie stareInspectie,
        Verdict verdict,
        BigDecimal nivelAql,
        Integer dimensiuneEsantion,
        Integer numarAcceptare,
        Integer numarRespingere,
        Integer defecteGasite
) implements Serializable {}

