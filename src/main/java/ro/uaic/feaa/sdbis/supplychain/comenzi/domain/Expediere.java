package ro.uaic.feaa.sdbis.supplychain.comenzi.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.IdLot;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainEntity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Entitate componenta normalizata (1NF) a agregatului ComandaAprovizionare.
 * Asociaza loturile livrate prin intermediul tabelei normalizate comenzi_expedieri_loturi.
 */
@DomainEntity
@Entity
@Table(name = "comenzi_expedieri")
public class Expediere implements Serializable {
    private static final long serialVersionUID = 1L;


    @Id
    @Column(name = "id", nullable = false)
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comanda_id")
    private ComandaAprovizionare comanda;

    @NotBlank
    @Column(name = "transportator", nullable = false)
    private String transportator;

    @NotBlank
    @Column(name = "nr_awb", nullable = false)
    private String nrAWB;

    @NotNull
    @Column(name = "data_expediere", nullable = false)
    private LocalDate dataExpediere;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "comenzi_expedieri_loturi", joinColumns = @JoinColumn(name = "expediere_id"))
    @AttributeOverride(name = "value", column = @Column(name = "lot_id", nullable = false))
    @SuppressWarnings("serial") // Collection interface Set does not implement Serializable directly; runtime instance is HashSet
    private Set<IdLot> loturi = new HashSet<>();

    protected Expediere() {}

    public Expediere(String transportator, String nrAWB, LocalDate dataExpediere, Set<IdLot> loturi) {
        if (transportator == null || transportator.trim().isEmpty()) {
            throw new IllegalArgumentException("transportator cannot be null or empty");
        }
        if (nrAWB == null || nrAWB.trim().isEmpty()) {
            throw new IllegalArgumentException("nrAWB cannot be null or empty");
        }
        this.id = UUID.randomUUID();
        this.transportator = transportator.trim();
        this.nrAWB = nrAWB.trim();
        this.dataExpediere = Objects.requireNonNull(dataExpediere, "dataExpediere cannot be null");
        if (loturi != null && !loturi.isEmpty()) {
            this.loturi.addAll(loturi);
        }
    }

    public Expediere(String transportator, String nrAWB, LocalDate dataExpediere, Collection<IdLot> loturi) {
        this(transportator, nrAWB, dataExpediere, loturi != null ? new HashSet<>(loturi) : Collections.<IdLot>emptySet());
    }

    public Expediere(ComandaAprovizionare comanda, String transportator, String nrAWB, LocalDate dataExpediere, Set<IdLot> loturi) {
        this(transportator, nrAWB, dataExpediere, loturi);
        this.comanda = comanda;
    }

    public Expediere(ComandaAprovizionare comanda, String transportator, String nrAWB, LocalDate dataExpediere, Collection<IdLot> loturi) {
        this(comanda, transportator, nrAWB, dataExpediere, loturi != null ? new HashSet<>(loturi) : Collections.<IdLot>emptySet());
    }

    public void ataseazaComanda(ComandaAprovizionare comanda) {
        this.comanda = comanda;
    }

    public UUID getId() {
        return id;
    }

    public ComandaAprovizionare getComanda() {
        return comanda;
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
        return Collections.unmodifiableSet(loturi);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expediere expediere = (Expediere) o;
        return Objects.equals(id, expediere.id) ||
               (Objects.equals(transportator, expediere.transportator) &&
                Objects.equals(nrAWB, expediere.nrAWB) &&
                Objects.equals(dataExpediere, expediere.dataExpediere));
    }

    @Override
    public int hashCode() {
        return Objects.hash(transportator, nrAWB, dataExpediere);
    }

    @Override
    public String toString() {
        return "Expediere[id=" + id + ", nrAWB=" + nrAWB + ", transportator=" + transportator + ", data=" + dataExpediere + "]";
    }
}
