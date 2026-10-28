package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.ValueObject;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@ValueObject
@Embeddable
public final class Expediere implements Serializable {

    @NotBlank
    @Column(name = "transportator", nullable = false)
    private String transportator;

    @NotBlank
    @Column(name = "nr_awb", nullable = false)
    private String nrAWB;

    @NotNull
    @Column(name = "data_expediere", nullable = false)
    private LocalDate dataExpediere;

    @Column(name = "loturi_ids", length = 2000)
    private String loturiIds;

    protected Expediere() {}

    public Expediere(String transportator, String nrAWB, LocalDate dataExpediere, Set<IdLot> loturi) {
        if (transportator == null || transportator.trim().isEmpty()) {
            throw new IllegalArgumentException("transportator cannot be null or empty");
        }
        if (nrAWB == null || nrAWB.trim().isEmpty()) {
            throw new IllegalArgumentException("nrAWB cannot be null or empty");
        }
        this.transportator = transportator.trim();
        this.nrAWB = nrAWB.trim();
        this.dataExpediere = Objects.requireNonNull(dataExpediere, "dataExpediere cannot be null");
        if (loturi != null && !loturi.isEmpty()) {
            this.loturiIds = loturi.stream()
                .map(l -> l.getValue().toString())
                .collect(Collectors.joining(","));
        } else {
            this.loturiIds = "";
        }
    }

    public String getTransportator() {
        return transportator;
    }

    public String getNrAWB() {
        return nrAWB;
    }

    public LocalDate getDataExpediere() {
        return dataExpediere;
    }

    public Set<IdLot> getLoturi() {
        if (loturiIds == null || loturiIds.trim().isEmpty()) {
            return Collections.emptySet();
        }
        return Arrays.stream(loturiIds.split(","))
            .filter(s -> !s.trim().isEmpty())
            .map(s -> IdLot.of(UUID.fromString(s.trim())))
            .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Expediere expediere)) return false;
        return Objects.equals(transportator, expediere.transportator) &&
               Objects.equals(nrAWB, expediere.nrAWB) &&
               Objects.equals(dataExpediere, expediere.dataExpediere) &&
               Objects.equals(loturiIds, expediere.loturiIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transportator, nrAWB, dataExpediere, loturiIds);
    }

    @Override
    public String toString() {
        return "Expediere[nrAWB=" + nrAWB + ", transportator=" + transportator + ", data=" + dataExpediere + "]";
    }
}
