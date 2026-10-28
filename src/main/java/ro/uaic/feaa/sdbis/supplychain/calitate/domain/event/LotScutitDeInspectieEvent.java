package ro.uaic.feaa.sdbis.supplychain.calitate.domain.event;

import ro.uaic.feaa.sdbis.supplychain.shared.DomainEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;

import java.time.Instant;
import java.util.UUID;

/**
 * Eveniment emis cand un lot este scutit de inspectie fizica AQL
 * (ex: furnizor de clasa A cu istoric impecabil conform Figurii 1, Tabelului 1.3 si Figurii 20 din Project_OOAD.docx).
 */
public record LotScutitDeInspectieEvent(
    UUID eventId,
    Instant occurredOn,
    IdLot lotId,
    IdComanda comandaId,
    IdFurnizor furnizorId,
    String motiv
) implements DomainEvent {}
