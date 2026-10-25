package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public record MetriciLivrareVO(
    IdFurnizor furnizorId,
    CodMaterial material,
    LocalDate dataComanda,
    LocalDate dataLivrarePromisa
) implements Serializable {
    public MetriciLivrareVO {
        Objects.requireNonNull(furnizorId, "furnizorId cannot be null");
        Objects.requireNonNull(material, "material cannot be null");
        Objects.requireNonNull(dataComanda, "dataComanda cannot be null");
    }
}
