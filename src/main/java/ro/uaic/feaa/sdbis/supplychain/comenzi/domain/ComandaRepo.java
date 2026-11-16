package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainRepository;
import java.util.List;
import java.util.Optional;

@DomainRepository
public interface ComandaRepo {
    void add(ComandaAprovizionare comanda);
    Optional<ComandaAprovizionare> findById(IdComanda id);
    ComandaAprovizionare save(ComandaAprovizionare comanda);
    void delete(ComandaAprovizionare comanda);
    boolean existaNumarComanda(String numarComanda);
    List<ComandaAprovizionare> findAll();
    List<ComandaAprovizionare> findByFurnizorId(IdFurnizor furnizorId);
    List<ComandaAprovizionare> findComenzi(IdFurnizor furnizorId, StareComanda stare, int page, int size);
    List<ComandaAprovizionare> findAllWithLiniiAndLoturi(IdFurnizor furnizorId);
    List<ComandaAprovizionare> findComenziNeconfirmateInainteDe(StareComanda stare, java.time.LocalDate dataLimita);
    Optional<ComandaAprovizionare> findByCodLot(String codLot);
}
