package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.event.PlataExecutataEvent;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.AggregateRoot;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Root pentru Ordinul de Plata (Figura 21 si 4.4).
 * Constructorul este exclusiv package-private (~): poate fi instantiat doar de FacturaFurnizor.creeazaOrdinPlata().
 */
@AggregateRoot
@Entity
@Table(name = "facturi_ordine_plata")
public class OrdinPlata implements Serializable {
    private static final long serialVersionUID = 1L;


    @NotNull
    @Id
    @Column(name = "id_ordin", nullable = false)
    private UUID idOrdin;

    @NotNull
    @Column(name = "factura_id", nullable = false)
    private UUID facturaId;

    @NotNull
    @Embedded
    @AttributeOverride(name = "valoare", column = @Column(name = "suma", nullable = false, precision = 18, scale = 4))
    @AttributeOverride(name = "moneda", column = @Column(name = "moneda", nullable = false, length = 3))
    private Suma suma;

    @NotNull
    @Column(name = "creat_de", nullable = false)
    private UUID creatDe;

    @Column(name = "aprobat_de")
    private UUID aprobatDe;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "stare_ordin", nullable = false, length = 50)
    private StareOrdinPlata stareOrdin;

    @Column(name = "referinta_bancara", length = 100)
    private String referintaBancara;

    @Column(name = "motiv_respingere", length = 255)
    private String motivRespingere;

    @Column(name = "data_aprobare")
    private Instant dataAprobare;

    @Column(name = "data_executie")
    private Instant dataExecutie;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @PrePersist
    protected void onPrePersist() {
        if (createdAt == null) createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onPreUpdate() {
        updatedAt = Instant.now();
    }

    protected OrdinPlata() {}

    // Constructor Package-Private (~)
    OrdinPlata(UUID facturaId, Suma suma, UUID creatDe) {
        this.idOrdin = UUID.randomUUID();
        this.facturaId = Objects.requireNonNull(facturaId);
        this.suma = Objects.requireNonNull(suma);
        this.creatDe = Objects.requireNonNull(creatDe);
        this.stareOrdin = StareOrdinPlata.Creat;
    }

    public boolean necesitaAprobareSuplimentara(Suma prag) {
        return this.suma.esteMaiMareDecat(prag);
    }

    public void trimiteLaAprobare() {
        this.stareOrdin = StareOrdinPlata.InAprobare;
    }

    public void aproba(UUID aprobator) {
        Objects.requireNonNull(aprobator, "aprobator cannot be null");
        if (aprobator.equals(this.creatDe)) {
            throw new IllegalStateException("Four-Eyes Principle: Aprobatorul (" + aprobator + ") trebuie sa fie diferit de creatorul ordinului (" + creatDe + ")");
        }
        this.aprobatDe = aprobator;
        this.dataAprobare = Instant.now();
        this.stareOrdin = StareOrdinPlata.Aprobat;
    }

    public static final Suma PRAG_APROBARE_DEFAULT = Suma.of(10000.0, "EUR");

    public void transmiteLaBanca() {
        if (this.stareOrdin == StareOrdinPlata.Creat) {
            Suma pragMoneda = Suma.of(10000.0, this.suma.getMoneda().getCurrencyCode());
            if (necesitaAprobareSuplimentara(pragMoneda)) {
                throw new IllegalStateException("Ordinul de plata cu valoarea de " + suma +
                        " depaseste pragul de autorizare si necesita aprobare financiara prealabila inainte de transmitere. Stare: " + stareOrdin);
            }
        }
        if (this.stareOrdin != StareOrdinPlata.Aprobat && this.stareOrdin != StareOrdinPlata.Creat) {
            throw new IllegalStateException("Doar un ordin aprobat sau creat sub prag poate fi transmis la banca. Stare: " + stareOrdin);
        }
        this.stareOrdin = StareOrdinPlata.TransmisLaBanca;
    }

    public PlataExecutataEvent inregistreazaExecutie() {
        if (this.stareOrdin != StareOrdinPlata.TransmisLaBanca) {
            throw new IllegalStateException("Doar un ordin transmis la banca poate fi executat. Stare curenta: " + stareOrdin);
        }
        this.stareOrdin = StareOrdinPlata.Executat;
        this.dataExecutie = Instant.now();
        return new PlataExecutataEvent(UUID.randomUUID(), Instant.now(), this.idOrdin, this.facturaId, this.suma);
    }

    public void respingeDeBanca(String motiv) {
        if (this.stareOrdin == StareOrdinPlata.Executat) {
            throw new IllegalStateException("Tranzitie invalida: Nu se poate respinge de catre banca un ordin deja executat!");
        }
        if (this.stareOrdin != StareOrdinPlata.TransmisLaBanca) {
            throw new IllegalStateException("Doar un ordin transmis la banca poate fi respins de banca. Stare curenta: " + stareOrdin);
        }
        this.motivRespingere = motiv;
        this.stareOrdin = StareOrdinPlata.RespinsDeBanca;
    }

    public void ataseazaReferintaBancara(String ref) {
        this.referintaBancara = ref;
    }

    public String getReferintaBancara() {
        return referintaBancara;
    }

    public String getMotivRespingere() {
        return motivRespingere;
    }

    public Instant getDataAprobare() {
        return dataAprobare;
    }

    public Instant getDataExecutie() {
        return dataExecutie;
    }

    public void anuleaza() {
        if (this.stareOrdin == StareOrdinPlata.Anulat) {
            throw new IllegalStateException("Tranzitie invalida: Ordinul de plata este deja anulat!");
        }
        if (this.stareOrdin == StareOrdinPlata.Executat || this.stareOrdin == StareOrdinPlata.TransmisLaBanca) {
            throw new IllegalStateException("Nu se poate anula un ordin in starea: " + stareOrdin);
        }
        this.stareOrdin = StareOrdinPlata.Anulat;
    }

    public UUID getIdOrdin() { return idOrdin; }
    public UUID getFacturaId() { return facturaId; }
    public Suma getSuma() { return suma; }
    public UUID getCreatDe() { return creatDe; }
    public UUID getAprobatDe() { return aprobatDe; }
    public StareOrdinPlata getStareOrdin() { return stareOrdin; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrdinPlata that)) return false;
        return getIdOrdin() != null && Objects.equals(getIdOrdin(), that.getIdOrdin());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "OrdinPlata[id=" + idOrdin + ", factura=" + facturaId + ", suma=" + suma + ", stare=" + stareOrdin + "]";
    }
}
