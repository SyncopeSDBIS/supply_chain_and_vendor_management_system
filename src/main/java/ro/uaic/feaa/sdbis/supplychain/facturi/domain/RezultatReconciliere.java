package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public record RezultatReconciliere(
    boolean aprobata,
    List<String> diferente
) implements Serializable {
    public RezultatReconciliere {
        if (diferente == null) diferente = Collections.emptyList();
    }
}
