package ro.uaic.feaa.sdbis.supplychain.comenzi.domain.event;

import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.Cantitate;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;

import java.time.Instant;
import java.util.UUID;

public record ComandaReceptionataEvent(
    UUID eventId,
    Instant occurredOn,
    IdComanda comandaId,
    IdLot lotId,
    CodMaterial material,
    Cantitate cantitatePrimita,
    boolean receptieTotala
) implements DomainEvent {}
