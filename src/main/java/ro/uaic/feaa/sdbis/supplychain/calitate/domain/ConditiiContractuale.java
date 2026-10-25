package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Embeddable
public class ConditiiContractuale implements Serializable {
    private int termenPlataZile;
    private Currency moneda;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<CodMaterial, BigDecimal> preturiNegociate = new HashMap<>();

    protected ConditiiContractuale() {}

    public ConditiiContractuale(int termenPlataZile, Currency moneda, Map<CodMaterial, BigDecimal> preturiNegociate) {
        this.termenPlataZile = termenPlataZile;
        this.moneda = Objects.requireNonNull(moneda, "moneda cannot be null");
        if (preturiNegociate != null) {
            this.preturiNegociate.putAll(preturiNegociate);
        }
    }

    public int getTermenPlataZile() { return termenPlataZile; }
    public Currency getMoneda() { return moneda; }
    public Map<CodMaterial, BigDecimal> getPreturiNegociate() { return preturiNegociate; }
}
