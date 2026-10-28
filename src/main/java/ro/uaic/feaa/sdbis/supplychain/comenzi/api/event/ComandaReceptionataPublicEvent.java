package ro.uaic.feaa.sdbis.supplychain.comenzi.api.event;

import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ComandaReceptionataPublicEvent(
    UUID eventId,
    Instant occurredOn,
    IdComanda comandaId,
    IdLot lotId,
    CodMaterial material,
    BigDecimal cantitatePrimita,
    String unitateMasura,
    boolean receptieTotala
) implements DomainEvent, Serializable {}
