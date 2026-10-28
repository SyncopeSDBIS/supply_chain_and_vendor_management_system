package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@ValueObject
public record PerformantaLivrariVO(
    IdFurnizor furnizorId,
    Perioada luna,
    BigDecimal otif,
    BigDecimal conformitatePret
) implements Serializable {
    public PerformantaLivrariVO {
        Objects.requireNonNull(furnizorId, "furnizorId cannot be null");
        Objects.requireNonNull(luna, "luna cannot be null");
        Objects.requireNonNull(otif, "otif cannot be null");
        Objects.requireNonNull(conformitatePret, "conformitatePret cannot be null");
    }
}
