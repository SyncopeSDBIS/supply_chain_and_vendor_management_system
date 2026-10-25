package ro.uaic.feaa.sdbis.supplychain.comenzi.application.query;

import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.StareLot;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TrasabilitateLotView(
    UUID idLot,
    String codLot,
    String codMaterial,
    BigDecimal cantitateLot,
    BigDecimal cantitatePrimita,
    LocalDate dataProductie,
    LocalDate dataExpirare,
    StareLot stareLot,
    UUID comandaId,
    String numarComanda,
    UUID furnizorId,
    String depozitDestinatie,
    String transportatorAWB
) {}
