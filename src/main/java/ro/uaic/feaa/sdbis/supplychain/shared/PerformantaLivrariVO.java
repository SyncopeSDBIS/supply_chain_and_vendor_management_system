package ro.uaic.feaa.sdbis.supplychain.shared;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object reprezentand performanta de livrare a unui furnizor intr-o perioada (OTIF si pret).
 * Decuplat in shared pentru a permite Inversion of Control intre Calitate si Comenzi.
 */
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

