package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@ValueObject
@Embeddable
public final class Certificare implements Serializable {

    @NotBlank
    @Column(name = "denumire", nullable = false)
    private String denumire;

    @NotNull
    @Column(name = "data_expirare", nullable = false)
    private LocalDate dataExpirare;

    protected Certificare() {}

    public Certificare(String denumire, LocalDate dataExpirare) {
        if (denumire == null || denumire.trim().isEmpty()) {
            throw new IllegalArgumentException("Denumire cannot be null or empty");
        }
        this.denumire = denumire.trim();
        this.dataExpirare = Objects.requireNonNull(dataExpirare, "dataExpirare cannot be null");
    }

    public boolean esteValidaPe(Perioada p) {
        Objects.requireNonNull(p, "perioada cannot be null");
        return !dataExpirare.isBefore(p.getDataFinalizare());
    }

    public boolean esteExpirataIn(LocalDate data) {
        return dataExpirare.isBefore(data);
    }

    public String getDenumire() { return denumire; }
    public LocalDate getDataExpirare() { return dataExpirare; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Certificare that)) return false;
        return Objects.equals(denumire, that.denumire) &&
               Objects.equals(dataExpirare, that.dataExpirare);
    }

    @Override
    public int hashCode() {
        return Objects.hash(denumire, dataExpirare);
    }

    @Override
    public String toString() {
        return denumire + " (Expirare: " + dataExpirare + ")";
    }
}
