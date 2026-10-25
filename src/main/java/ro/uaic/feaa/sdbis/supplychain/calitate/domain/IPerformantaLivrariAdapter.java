package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;

public interface IPerformantaLivrariAdapter {
    PerformantaLivrariVO obtinePerformanta(IdFurnizor furnizorId, Perioada luna);
}
