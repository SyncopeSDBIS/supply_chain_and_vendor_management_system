package ro.uaic.feaa.sdbis.supplychain.comenzi.api.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ComandaReceptionataPublicEvent(
        @NotNull UUID eventId,
        @NotNull Instant occurredOn,
        @NotNull IdComanda comandaId,
        @NotNull IdLot lotId,
        @NotNull CodMaterial material,
        @NotNull @Positive BigDecimal cantitatePrimita,
        @NotBlank String unitateMasura,
        boolean receptieTotala
) implements DomainEvent, Serializable {}
