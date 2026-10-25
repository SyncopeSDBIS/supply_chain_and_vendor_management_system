package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public final class CodMaterial implements Serializable {
    private String cod;

    protected CodMaterial() {}

    public CodMaterial(String cod) {
        if (cod == null || cod.trim().isEmpty()) {
            throw new IllegalArgumentException("CodMaterial cannot be null or empty");
        }
        this.cod = cod.trim().toUpperCase();
    }

    public static CodMaterial of(String cod) {
        return new CodMaterial(cod);
    }

    public String getCod() {
        return cod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodMaterial that)) return false;
        return Objects.equals(cod, that.cod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cod);
    }

    @Override
    public String toString() {
        return cod != null ? cod : "";
    }
}
