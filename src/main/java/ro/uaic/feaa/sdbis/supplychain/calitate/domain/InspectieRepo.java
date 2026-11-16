package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DomainRepository
public interface InspectieRepo {
    void add(InspectieCalitate i);
    Optional<InspectieCalitate> findById(UUID id);
    InspectieCalitate save(InspectieCalitate i);
    List<InspectieCalitate> findAll();
    List<InspectieCalitate> findInspectii(IdFurnizor furnizorId, UUID inspectorId, StareInspectie stare, int page, int size);
    BigDecimal calculeazaRataRespingere(IdFurnizor furnizorId, LocalDate deLa, LocalDate panaLa);
    List<InspectieCalitate> findByFurnizorIds(List<IdFurnizor> furnizorIds);
    long countTotal();
    long countByVerdict(Verdict verdict);
}
