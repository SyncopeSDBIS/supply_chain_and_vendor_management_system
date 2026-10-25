package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public record IstoricLivrariVO(List<Integer> leadTimeZile) implements Serializable {
    public IstoricLivrariVO {
        if (leadTimeZile == null) {
            leadTimeZile = Collections.emptyList();
        }
    }
}
