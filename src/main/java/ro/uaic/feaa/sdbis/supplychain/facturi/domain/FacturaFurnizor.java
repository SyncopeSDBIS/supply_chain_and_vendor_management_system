package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.AggregateRoot;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Aggregate Root pentru Factura Furnizor (Figura 21 si 24).
 * Invariant strict: Creeaza OrdinPlata DOAR daca este AprobataLaPlata sau PartialPlatita,
 * iar suma ordinului <= restDePlata.
 */
@AggregateRoot
@Entity
@Table(name = "facturi_furnizori")
public class FacturaFurnizor implements Serializable {

    @NotNull
    @Id
    private UUID idFactura;

    @NotBlank
    @Column(nullable = false)
    private String serie;

    @NotBlank
    @Column(nullable = false)
    private String numar;

    @NotNull
    @Column(nullable = false)
    private LocalDate dataEmitere;

    @NotNull
    @Column(nullable = false)
    private LocalDate dataScadenta;

    @NotNull
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "comanda_id"))
    private IdComanda comandaId;

    @NotNull
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "furnizor_id"))
    private IdFurnizor furnizorId;

    @NotEmpty
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "facturi_linii", joinColumns = @JoinColumn(name = "factura_id"))
    private List<LinieFactura> linii = new ArrayList<>();

    @NotNull
    @Embedded
    private ValoareFactura valoare;

    @NotNull
    @Embedded
    @AttributeOverride(name = "valoare", column = @Column(name = "suma_platita_valoare"))
    @AttributeOverride(name = "moneda", column = @Column(name = "suma_platita_moneda"))
    private Suma sumaPlatita;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StareFactura stareFactura;

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
        this.stareFactura = StareFactura.Blocata;
    }

    public void contesta(String motiv) {
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
        this.sumaPlatita = this.sumaPlatita.adauga(suma);
        Suma rest = calculeazaRestDePlata();
        if (rest.getValoare().compareTo(BigDecimal.ZERO) == 0) {
            this.stareFactura = StareFactura.Platita;
        } else {
            this.stareFactura = StareFactura.PartialPlatita;
        }
    }

    public void anuleaza() {
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
    public StareFactura getStareFactura() { return stareFactura; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FacturaFurnizor that)) return false;
        return Objects.equals(idFactura, that.idFactura);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idFactura);
    }

    @Override
    public String toString() {
        return "FacturaFurnizor[id=" + idFactura + ", serie=" + serie + ", nr=" + numar + ", stare=" + stareFactura + "]";
    }
}
