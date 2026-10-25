package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public record LinieSumar(
    CodMaterial material,
    BigDecimal cantitateComandata,
    BigDecimal pretUnitar
) implements Serializable {
    public LinieSumar {
        Objects.requireNonNull(material, "material cannot be null");
        Objects.requireNonNull(cantitateComandata, "cantitateComandata cannot be null");
        Objects.requireNonNull(pretUnitar, "pretUnitar cannot be null");
    }
}
