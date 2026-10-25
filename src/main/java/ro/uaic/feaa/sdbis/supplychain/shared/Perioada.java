package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public final class Perioada implements Serializable {
    private LocalDate dataInceput;
    private LocalDate dataFinalizare;

    protected Perioada() {}

    public Perioada(LocalDate dataInceput, LocalDate dataFinalizare) {
        Objects.requireNonNull(dataInceput, "dataInceput cannot be null");
        Objects.requireNonNull(dataFinalizare, "dataFinalizare cannot be null");
        if (dataFinalizare.isBefore(dataInceput)) {
            throw new IllegalArgumentException("dataFinalizare must be >= dataInceput");
        }
        this.dataInceput = dataInceput;
        this.dataFinalizare = dataFinalizare;
    }

    public static Perioada of(LocalDate dataInceput, LocalDate dataFinalizare) {
        return new Perioada(dataInceput, dataFinalizare);
    }

    public static Perioada ofLuna(int an, int luna) {
        LocalDate start = LocalDate.of(an, luna, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return new Perioada(start, end);
    }

    public static Perioada lunaCurenta() {
        LocalDate now = LocalDate.now();
        return ofLuna(now.getYear(), now.getMonthValue());
    }

    public static Perioada zileUrmatoare(int days) {
        LocalDate start = LocalDate.now();
        return new Perioada(start, start.plusDays(days));
    }

    public boolean esteInclusaIn(Perioada alta) {
        Objects.requireNonNull(alta, "alta perioada cannot be null");
        return !this.dataInceput.isBefore(alta.dataInceput) && !this.dataFinalizare.isAfter(alta.dataFinalizare);
    }

    public boolean includeData(LocalDate data) {
        Objects.requireNonNull(data, "data cannot be null");
        return !data.isBefore(dataInceput) && !data.isAfter(dataFinalizare);
    }

    public LocalDate getDataInceput() {
        return dataInceput;
    }

    public LocalDate getDataFinalizare() {
        return dataFinalizare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Perioada perioada)) return false;
        return Objects.equals(dataInceput, perioada.dataInceput) && Objects.equals(dataFinalizare, perioada.dataFinalizare);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataInceput, dataFinalizare);
    }

    @Override
    public String toString() {
        return "[" + dataInceput + " .. " + dataFinalizare + "]";
    }
}
