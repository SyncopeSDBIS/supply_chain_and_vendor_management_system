package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@ValueObject
public record RezultatReconciliere(
    boolean aprobata,
    List<String> diferente
) implements Serializable {
    public RezultatReconciliere {
        if (diferente == null) diferente = Collections.emptyList();
    }
}
