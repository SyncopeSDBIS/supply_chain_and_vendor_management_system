package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ValueObject
@Embeddable
public class ConditiiContractuale implements Serializable {

    @Min(0)
    @Column(name = "termen_plata_zile")
    private int termenPlataZile;

    @NotNull
    @Column(name = "moneda")
    private Currency moneda;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "calitate_furnizori_preturi", joinColumns = @JoinColumn(name = "furnizor_id"))
    @Column(name = "pret_negociat")
    private Map<CodMaterial, BigDecimal> preturiNegociate = new HashMap<>();

    protected ConditiiContractuale() {}

    public ConditiiContractuale(int termenPlataZile, Currency moneda, Map<CodMaterial, BigDecimal> preturiNegociate) {
        if (termenPlataZile < 0) {
            throw new IllegalArgumentException("Termenul de plata nu poate fi negativ");
        }
        this.termenPlataZile = termenPlataZile;
        this.moneda = Objects.requireNonNull(moneda, "moneda cannot be null");
        if (preturiNegociate != null) {
            this.preturiNegociate.putAll(preturiNegociate);
        }
    }

    public int getTermenPlataZile() { return termenPlataZile; }
    public Currency getMoneda() { return moneda; }
    public Map<CodMaterial, BigDecimal> getPreturiNegociate() { return Collections.unmodifiableMap(preturiNegociate); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConditiiContractuale that)) return false;
        return termenPlataZile == that.termenPlataZile &&
               Objects.equals(moneda, that.moneda) &&
               Objects.equals(preturiNegociate, that.preturiNegociate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(termenPlataZile, moneda, preturiNegociate);
    }

    @Override
    public String toString() {
        return "Conditii[termen=" + termenPlataZile + " zile, moneda=" + (moneda != null ? moneda.getCurrencyCode() : "") + "]";
    }
}
