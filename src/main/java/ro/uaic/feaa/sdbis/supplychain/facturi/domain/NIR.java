package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.AggregateRoot;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@AggregateRoot
@Entity
@Table(name = "facturi_nir")
public class NIR implements Serializable {
    private static final long serialVersionUID = 1L;


    @NotNull
    @Id
    @Column(name = "id_nir", nullable = false)
    private UUID idNIR;

    @NotBlank
    @Column(name = "numar_nir", nullable = false, unique = true, length = 100)
    private String numarNIR;

    @NotNull
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "comanda_id", nullable = false))
    private IdComanda comandaId;

    @NotNull
    @Column(name = "event_sursa_id", nullable = false, unique = true)
    private UUID eventSursaId; // Constrangere UNIQUE pentru garantia idempotentei

    @NotNull
    @Column(name = "data_receptie", nullable = false)
    private LocalDate dataReceptie;

    @NotEmpty
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "facturi_nir_linii", joinColumns = @JoinColumn(name = "nir_id"))
    @OrderColumn(name = "linie_idx")
    @SuppressWarnings("serial") // Collection interface List does not implement Serializable directly; runtime instance is ArrayList
    private List<LinieNIR> linii = new ArrayList<>();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "stare_nir", nullable = false, length = 50)
    private StareNIR stareNIR;

    @Column(name = "motiv_stornare", length = 500)
    private String motivStornare;

    @Column(name = "created_at")
    private java.time.Instant createdAt = java.time.Instant.now();

    @Column(name = "updated_at")
    private java.time.Instant updatedAt = java.time.Instant.now();

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @PrePersist
    protected void onPrePersist() {
        if (createdAt == null) createdAt = java.time.Instant.now();
        updatedAt = java.time.Instant.now();
    }

    @PreUpdate
    protected void onPreUpdate() {
        updatedAt = java.time.Instant.now();
    }

    protected NIR() {}

    public NIR(UUID idNIR, String numarNIR, IdComanda comandaId, UUID eventSursaId, LocalDate dataReceptie, List<LinieNIR> linii) {
        this.idNIR = Objects.requireNonNull(idNIR);
        this.numarNIR = Objects.requireNonNull(numarNIR);
        this.comandaId = Objects.requireNonNull(comandaId);
        this.eventSursaId = Objects.requireNonNull(eventSursaId);
        this.dataReceptie = Objects.requireNonNull(dataReceptie);
        if (linii != null) this.linii.addAll(linii);
        this.stareNIR = StareNIR.Generat;
    }

    public void trimiteLaReconciliere() {
        if (this.stareNIR == StareNIR.Stornat || this.stareNIR == StareNIR.Contabilizat) {
            throw new IllegalStateException("Tranzitie invalida: NIR-ul nu poate fi trimis la reconciliere din starea " + stareNIR);
        }
        this.stareNIR = StareNIR.InReconciliere;
    }

    public void marcheazaDiscrepante() {
        if (this.stareNIR == StareNIR.Stornat || this.stareNIR == StareNIR.Contabilizat) {
            throw new IllegalStateException("Tranzitie invalida: NIR-ul nu poate fi marcat cu discrepante din starea " + stareNIR);
        }
        this.stareNIR = StareNIR.CuDiscrepante;
    }

    public void reconciliaza() {
        if (this.stareNIR == StareNIR.Stornat || this.stareNIR == StareNIR.Contabilizat) {
            throw new IllegalStateException("Tranzitie invalida: NIR-ul nu poate fi reconciliat din starea " + stareNIR);
        }
        this.stareNIR = StareNIR.Reconciliat;
    }

    public void contabilizeaza() {
        if (this.stareNIR != StareNIR.Reconciliat) {
            throw new IllegalStateException("Doar un NIR reconciliat poate fi contabilizat. Stare: " + stareNIR);
        }
        this.stareNIR = StareNIR.Contabilizat;
    }

    public void storneaza(String motiv) {
        if (this.stareNIR == StareNIR.Stornat) {
            throw new IllegalStateException("Tranzitie invalida: NIR-ul este deja stornat.");
        }
        if (this.stareNIR == StareNIR.Contabilizat) {
            throw new IllegalStateException("Tranzitie invalida: NIR-ul contabilizat nu poate fi stornat direct in-place; necesita document contabil storno distinct.");
        }
        this.motivStornare = motiv;
        this.stareNIR = StareNIR.Stornat;
    }

    public BigDecimal calculeazaCantitateReceptionata(CodMaterial material) {
        BigDecimal total = BigDecimal.ZERO;
        for (LinieNIR l : linii) {
            if (l.getMaterial().equals(material)) {
                total = total.add(l.getCantitateReceptionata());
            }
        }
        return total;
    }

    public UUID getIdNIR() { return idNIR; }
    public String getNumarNIR() { return numarNIR; }
    public String getMotivStornare() { return motivStornare; }
    public IdComanda getComandaId() { return comandaId; }
    public UUID getEventSursaId() { return eventSursaId; }
    public LocalDate getDataReceptie() { return dataReceptie; }
    public List<LinieNIR> getLinii() { return Collections.unmodifiableList(linii); }
    public StareNIR getStareNIR() { return stareNIR; }
    public java.time.Instant getCreatedAt() { return createdAt; }
    public java.time.Instant getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NIR nir)) return false;
        return getIdNIR() != null && Objects.equals(getIdNIR(), nir.getIdNIR());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "NIR[id=" + idNIR + ", nr=" + numarNIR + ", stare=" + stareNIR + ", dataReceptie=" + dataReceptie + "]";
    }
}
