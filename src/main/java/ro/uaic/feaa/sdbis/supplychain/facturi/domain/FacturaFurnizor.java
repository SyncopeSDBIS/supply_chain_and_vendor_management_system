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
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.AggregateRoot;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Root pentru Factura Furnizor (Figura 21 si 24).
 * Invariant strict: Creeaza OrdinPlata DOAR daca este AprobataLaPlata sau PartialPlatita,
 * iar suma ordinului <= restDePlata.
 */
@AggregateRoot
@Entity
@Table(name = "facturi_furnizori", uniqueConstraints = {
    @UniqueConstraint(name = "uk_facturi_furnizor_serie_numar", columnNames = {"furnizor_id", "serie", "numar"})
})
public class FacturaFurnizor implements Serializable {
    private static final long serialVersionUID = 1L;


    @NotNull
    @Id
    @Column(name = "id_factura", nullable = false)
    private UUID idFactura;

    @NotBlank
    @Column(name = "serie", nullable = false, length = 50)
    private String serie;

    @NotBlank
    @Column(name = "numar", nullable = false, length = 50)
    private String numar;

    @NotNull
    @Column(name = "data_emitere", nullable = false)
    private LocalDate dataEmitere;

    @NotNull
    @Column(name = "data_scadenta", nullable = false)
    private LocalDate dataScadenta;

    @NotNull
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "comanda_id", nullable = false))
    private IdComanda comandaId;

    @NotNull
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "furnizor_id", nullable = false))
    private IdFurnizor furnizorId;

    @NotEmpty
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "facturi_linii", joinColumns = @JoinColumn(name = "factura_id"))
    @OrderColumn(name = "linie_idx")
    @SuppressWarnings("serial") // Collection interface List does not implement Serializable directly; runtime instance is ArrayList
    private List<LinieFactura> linii = new ArrayList<>();

    @NotNull
    @Embedded
    private ValoareFactura valoare;

    @NotNull
    @Embedded
    @AttributeOverride(name = "valoare", column = @Column(name = "suma_platita", nullable = false, precision = 18, scale = 4))
    @AttributeOverride(name = "moneda", column = @Column(name = "moneda", nullable = false, length = 3))
    private Suma sumaPlatita;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "stare_factura", nullable = false, length = 50)
    private StareFactura stareFactura;

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

    protected FacturaFurnizor() {}

    public FacturaFurnizor(UUID idFactura, String serie, String numar, LocalDate dataEmitere, LocalDate dataScadenta,
                          IdComanda comandaId, IdFurnizor furnizorId, List<LinieFactura> linii, Currency moneda) {
        this.idFactura = Objects.requireNonNull(idFactura);
        this.serie = Objects.requireNonNull(serie);
        this.numar = Objects.requireNonNull(numar);
        this.dataEmitere = Objects.requireNonNull(dataEmitere);
        this.dataScadenta = Objects.requireNonNull(dataScadenta);
        this.comandaId = Objects.requireNonNull(comandaId);
        this.furnizorId = Objects.requireNonNull(furnizorId);
        if (linii != null) this.linii.addAll(linii);
        this.valoare = ValoareFactura.calculeaza(this.linii);
        this.sumaPlatita = Suma.zero(moneda);
        this.stareFactura = StareFactura.Inregistrata;
    }

    public void trimiteLaVerificare() {
        if (this.stareFactura != StareFactura.Inregistrata && this.stareFactura != StareFactura.Blocata) {
            throw new IllegalStateException("Factura can be verified only from Inregistrata or Blocata. Current: " + stareFactura);
        }
        this.stareFactura = StareFactura.InVerificare;
    }

    public void aproba() {
        if (this.stareFactura != StareFactura.InVerificare) {
            throw new IllegalStateException("Factura can be approved only from InVerificare. Current: " + stareFactura);
        }
        this.stareFactura = StareFactura.AprobataLaPlata;
    }

    public void blocheaza(String motiv) {
        if (this.stareFactura == StareFactura.Platita || this.stareFactura == StareFactura.Anulata) {
            throw new IllegalStateException("Nu se poate bloca o factura in starea: " + stareFactura);
        }
        this.stareFactura = StareFactura.Blocata;
    }

    public void contesta(String motiv) {
        if (this.stareFactura == StareFactura.Platita || this.stareFactura == StareFactura.Anulata) {
            throw new IllegalStateException("Nu se poate contesta o factura in starea: " + stareFactura);
        }
        this.stareFactura = StareFactura.Contestata;
    }

    /**
     * FacturaFurnizor este singura poarta de creare pentru OrdinPlata (Control Financiar - Tabelul 2.3).
     */
    public OrdinPlata creeazaOrdinPlata(Suma suma, UUID creatDe) {
        if (!esteAprobataLaPlata() && this.stareFactura != StareFactura.PartialPlatita) {
            throw new IllegalStateException("Nu se poate crea ordin de plata pentru o factura neaprobata. Stare: " + stareFactura);
        }
        Suma restDePlata = calculeazaRestDePlata();
        if (suma.esteMaiMareDecat(restDePlata)) {
            throw new IllegalArgumentException("Suma ordinului (" + suma + ") depaseste restul de plata (" + restDePlata + ")");
        }
        return new OrdinPlata(this.idFactura, suma, creatDe);
    }

    public void inregistreazaPlata(Suma suma) {
        // FIX DDD-01: Gărzi invariante împotriva plății pe stări terminale și supra-plăților
        if (this.stareFactura == StareFactura.Anulata || this.stareFactura == StareFactura.Platita) {
            throw new IllegalStateException("Nu se poate inregistra o plata pe o factura in starea terminala: " + stareFactura);
        }
        Suma restDePlataCurent = calculeazaRestDePlata();
        if (suma.esteMaiMareDecat(restDePlataCurent)) {
            throw new IllegalArgumentException("Suma platii (" + suma + ") depaseste restul de plata al facturii (" + restDePlataCurent + ")");
        }
        this.sumaPlatita = this.sumaPlatita.adauga(suma);
        Suma rest = calculeazaRestDePlata();
        if (rest.getValoare().compareTo(BigDecimal.ZERO) == 0) {
            this.stareFactura = StareFactura.Platita;
        } else {
            this.stareFactura = StareFactura.PartialPlatita;
        }
    }

    public void anuleaza() {
        if (this.stareFactura == StareFactura.Platita || this.stareFactura == StareFactura.PartialPlatita || this.stareFactura == StareFactura.Anulata) {
            throw new IllegalStateException("Nu se poate anula o factura in starea: " + stareFactura);
        }
        this.stareFactura = StareFactura.Anulata;
    }

    public Suma calculeazaRestDePlata() {
        BigDecimal total = valoare.getValoareTotala();
        BigDecimal platit = sumaPlatita.getValoare();
        BigDecimal rest = total.subtract(platit);
        if (rest.compareTo(BigDecimal.ZERO) < 0) rest = BigDecimal.ZERO;
        return new Suma(rest, sumaPlatita.getMoneda());
    }

    public boolean esteAprobataLaPlata() {
        return this.stareFactura == StareFactura.AprobataLaPlata;
    }

    public UUID getIdFactura() { return idFactura; }
    public String getSerie() { return serie; }
    public String getNumar() { return numar; }
    public LocalDate getDataEmitere() { return dataEmitere; }
    public LocalDate getDataScadenta() { return dataScadenta; }
    public IdComanda getComandaId() { return comandaId; }
    public IdFurnizor getFurnizorId() { return furnizorId; }
    public List<LinieFactura> getLinii() { return Collections.unmodifiableList(linii); }
    public ValoareFactura getValoare() { return valoare; }
    public Suma getSumaPlatita() { return sumaPlatita; }
    public Currency getMoneda() { return sumaPlatita != null ? sumaPlatita.getMoneda() : Currency.getInstance("RON"); }
    public StareFactura getStareFactura() { return stareFactura; }
    public java.time.Instant getCreatedAt() { return createdAt; }
    public java.time.Instant getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FacturaFurnizor that)) return false;
        return getIdFactura() != null && Objects.equals(getIdFactura(), that.getIdFactura());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "FacturaFurnizor[id=" + idFactura + ", serie=" + serie + ", nr=" + numar + ", stare=" + stareFactura + "]";
    }
}
