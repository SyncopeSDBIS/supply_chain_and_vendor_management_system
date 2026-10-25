package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import java.util.List;
import java.util.Optional;

public interface FurnizorRepo {
    void add(Furnizor f);
    Optional<Furnizor> findById(IdFurnizor id);
    void save(Furnizor f);
    List<Furnizor> findAll();
}
