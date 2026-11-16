package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainRepository;
import java.util.List;
import java.util.Optional;

@DomainRepository
public interface FurnizorRepo {
    void add(Furnizor f);
    Optional<Furnizor> findById(IdFurnizor id);
    Furnizor save(Furnizor f);
    List<Furnizor> findAll();
    List<Furnizor> findEligibiliPentruComenzi();
    List<Furnizor> findFurnizori(StareFurnizor stare, String categorie, int page, int size);
    List<Furnizor> findFurnizori(StareFurnizor stare, String categorie, String search, int page, int size);
    long countTotal();
    long countByStare(StareFurnizor stare);
}
