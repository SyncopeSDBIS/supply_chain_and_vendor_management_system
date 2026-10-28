package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@ValueObject
@Embeddable
public final class EstimareLivrare implements Serializable {

    @NotNull
    @Column(name = "data_estimata")
    private LocalDate dataEstimata;

    @NotBlank
    @Column(name = "metoda")
    private String metoda;

    protected EstimareLivrare() {}

    public EstimareLivrare(LocalDate dataEstimata, String metoda) {
        this.dataEstimata = Objects.requireNonNull(dataEstimata, "dataEstimata cannot be null");
        if (metoda == null || metoda.trim().isEmpty()) {
            throw new IllegalArgumentException("metoda cannot be null or empty");
        }
        this.metoda = metoda.trim();
    }

    public boolean depasesteData(LocalDate ceruta) {
        Objects.requireNonNull(ceruta, "data ceruta cannot be null");
        return dataEstimata.isAfter(ceruta);
    }

    public LocalDate getDataEstimata() {
        return dataEstimata;
    }

    public String getMetoda() {
        return metoda;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EstimareLivrare that)) return false;
        return Objects.equals(dataEstimata, that.dataEstimata) &&
               Objects.equals(metoda, that.metoda);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataEstimata, metoda);
    }

    @Override
    public String toString() {
        return dataEstimata + " (" + metoda + ")";
    }
}
