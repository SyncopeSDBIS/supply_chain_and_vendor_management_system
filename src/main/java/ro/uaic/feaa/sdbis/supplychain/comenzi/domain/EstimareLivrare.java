package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public final class EstimareLivrare implements Serializable {
    private LocalDate dataEstimata;
    private String metoda;

    protected EstimareLivrare() {}

    public EstimareLivrare(LocalDate dataEstimata, String metoda) {
        this.dataEstimata = Objects.requireNonNull(dataEstimata, "dataEstimata cannot be null");
        this.metoda = Objects.requireNonNull(metoda, "metoda cannot be null");
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
    public String toString() {
        return dataEstimata + " (" + metoda + ")";
    }
}
