package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.CodMaterial;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.AggregateRoot;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@AggregateRoot
@Entity
@Table(name = "facturi_nir")
public class NIR implements Serializable {

    @NotNull
    @Id
    private UUID idNIR;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String numarNIR;

    @NotNull
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "comanda_id"))
    private IdComanda comandaId;

    @NotNull
    @Column(nullable = false, unique = true)
    private UUID eventSursaId; // Constrangere UNIQUE pentru garantia idempotentei

    @NotNull
    private LocalDate dataReceptie;

    @NotEmpty
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "facturi_nir_linii", joinColumns = @JoinColumn(name = "nir_id"))
    private List<LinieNIR> linii = new ArrayList<>();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StareNIR stareNIR;

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
        this.stareNIR = StareNIR.InReconciliere;
    }

    public void marcheazaDiscrepante() {
        this.stareNIR = StareNIR.CuDiscrepante;
    }

    public void reconciliaza() {
        this.stareNIR = StareNIR.Reconciliat;
    }

    public void contabilizeaza() {
        if (this.stareNIR != StareNIR.Reconciliat) {
            throw new IllegalStateException("Doar un NIR reconciliat poate fi contabilizat. Stare: " + stareNIR);
        }
        this.stareNIR = StareNIR.Contabilizat;
    }

    public void storneaza(String motiv) {
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
    public IdComanda getComandaId() { return comandaId; }
    public UUID getEventSursaId() { return eventSursaId; }
    public LocalDate getDataReceptie() { return dataReceptie; }
    public List<LinieNIR> getLinii() { return Collections.unmodifiableList(linii); }
    public StareNIR getStareNIR() { return stareNIR; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NIR nir)) return false;
        return Objects.equals(idNIR, nir.idNIR);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idNIR);
    }

    @Override
    public String toString() {
        return "NIR[id=" + idNIR + ", nr=" + numarNIR + ", stare=" + stareNIR + ", dataReceptie=" + dataReceptie + "]";
    }
}
