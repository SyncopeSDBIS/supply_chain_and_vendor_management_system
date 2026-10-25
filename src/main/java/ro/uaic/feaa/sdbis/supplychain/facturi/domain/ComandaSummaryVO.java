package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record ComandaSummaryVO(
    IdComanda comandaId,
    List<LinieSumar> linii
) implements Serializable {
    public ComandaSummaryVO {
        Objects.requireNonNull(comandaId, "comandaId cannot be null");
        if (linii == null) linii = Collections.emptyList();
    }
}
