package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import jakarta.persistence.*;
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

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

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
        this.stareOrdin = StareOrdinPlata.Aprobat;
    }

    public void transmiteLaBanca() {
        if (this.stareOrdin != StareOrdinPlata.Aprobat && this.stareOrdin != StareOrdinPlata.Creat) {
            throw new IllegalStateException("Doar un ordin aprobat sau creat poate fi transmis la banca. Stare: " + stareOrdin);
        }
        this.stareOrdin = StareOrdinPlata.TransmisLaBanca;
    }

    public PlataExecutataEvent inregistreazaExecutie() {
        this.stareOrdin = StareOrdinPlata.Executat;
        return new PlataExecutataEvent(UUID.randomUUID(), Instant.now(), this.idOrdin, this.facturaId, this.suma);
    }

    public void respingeDeBanca(String motiv) {
        this.stareOrdin = StareOrdinPlata.RespinsDeBanca;
    }

    public void anuleaza() {
        this.stareOrdin = StareOrdinPlata.Anulat;
    }

    public UUID getIdOrdin() { return idOrdin; }
    public UUID getFacturaId() { return facturaId; }
    public Suma getSuma() { return suma; }
    public UUID getCreatDe() { return creatDe; }
    public UUID getAprobatDe() { return aprobatDe; }
    public StareOrdinPlata getStareOrdin() { return stareOrdin; }
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
