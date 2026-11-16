package ro.uaic.feaa.sdbis.supplychain.shared;

import java.util.Optional;

public interface IFurnizorComandaAdapter {
    Optional<IdFurnizor> gasesteFurnizorComanda(IdComanda comandaId);
}
