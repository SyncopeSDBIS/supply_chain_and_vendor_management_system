package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;

public interface IstoricLivrariRepo {
    IstoricLivrariVO obtineIstoric(IdFurnizor furnizorId, CodMaterial material);
}
