package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.AggregateRoot;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@AggregateRoot
@Entity
@Table(name = "calitate_inspectori")
public class InspectorCalitate implements Serializable {

    @NotNull
    @Id
    @Column(nullable = false)
    private UUID idInspector;

    @NotNull
    @Embedded
    private FullName fullName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "calitate_inspectori_certificari", joinColumns = @JoinColumn(name = "inspector_id"))
    private List<Certificare> certificari = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "calitate_inspectori_disponibilitate", joinColumns = @JoinColumn(name = "inspector_id"))
    private List<Perioada> disponibilitate = new ArrayList<>();

    protected InspectorCalitate() {}

    public InspectorCalitate(UUID idInspector, FullName fullName) {
        this.idInspector = Objects.requireNonNull(idInspector, "idInspector cannot be null");
        this.fullName = Objects.requireNonNull(fullName, "fullName cannot be null");
    }

    public void adaugaCertificare(Certificare c) {
        this.certificari.add(Objects.requireNonNull(c, "certificare cannot be null"));
    }

    public void declaraDisponibilitate(Perioada p) {
        this.disponibilitate.add(Objects.requireNonNull(p, "perioada cannot be null"));
    }

    public boolean poateFiAlocat(Perioada p) {
        boolean certificareValida = certificari.stream().anyMatch(c -> c.esteValidaPe(p));
        boolean disponibil = disponibilitate.stream().anyMatch(d -> p.esteInclusaIn(d));
        return certificareValida && disponibil;
    }

    public UUID getIdInspector() { return idInspector; }
    public FullName getFullName() { return fullName; }
    public List<Certificare> getCertificari() { return Collections.unmodifiableList(certificari); }
    public List<Perioada> getDisponibilitate() { return Collections.unmodifiableList(disponibilitate); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InspectorCalitate that)) return false;
        return Objects.equals(idInspector, that.idInspector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idInspector);
    }

    @Override
    public String toString() {
        return "InspectorCalitate[id=" + idInspector + ", name=" + fullName + "]";
    }
}
