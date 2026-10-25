package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import java.util.List;
import java.util.Optional;

public interface ComandaRepo {
    void add(ComandaAprovizionare comanda);
    Optional<ComandaAprovizionare> findById(IdComanda id);
    void save(ComandaAprovizionare comanda);
    void delete(ComandaAprovizionare comanda);
    boolean existaNumarComanda(String numarComanda);
    List<ComandaAprovizionare> findAll();
}
