package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DomainRepository
public interface FacturaRepo {
    void add(FacturaFurnizor f);

    Optional<FacturaFurnizor> findById(UUID id);

    FacturaFurnizor save(FacturaFurnizor f);

    List<FacturaFurnizor> findAll();

    List<FacturaFurnizor> findByComandaId(IdComanda comandaId);

    List<FacturaFurnizor> findFacturiNeachitate();

    List<FacturaFurnizor> findFacturiNeachitate(int maxResults);

    List<FacturaFurnizor> findFacturiCuScadentaLa(List<StareFactura> stari, LocalDate dataScadenta);

    List<FacturaFurnizor> findFacturi(IdFurnizor furnizorId, IdComanda comandaId, StareFactura stare, int page, int size);
}
