package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@ValueObject
public record IstoricLivrariVO(List<Integer> leadTimeZile) implements Serializable {
    public IstoricLivrariVO {
        if (leadTimeZile == null) {
            leadTimeZile = Collections.emptyList();
        }
    }
}
