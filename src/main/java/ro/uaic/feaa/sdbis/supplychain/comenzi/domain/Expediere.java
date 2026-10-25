package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Expediere implements Serializable {
    private String transportator;
    private String nrAWB;
    private LocalDate dataExpediere;

    @Column(name = "loturi_ids", length = 2000)
    private String loturiIds;

    protected Expediere() {}

    public Expediere(String transportator, String nrAWB, LocalDate dataExpediere, Set<IdLot> loturi) {
        this.transportator = Objects.requireNonNull(transportator, "transportator cannot be null");
        this.nrAWB = Objects.requireNonNull(nrAWB, "nrAWB cannot be null");
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
}
