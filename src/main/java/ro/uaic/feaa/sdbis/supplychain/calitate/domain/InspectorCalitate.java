package ro.uaic.feaa.sdbis.supplychain.calitate.domain;

import jakarta.persistence.*;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "calitate_inspectori")
public class InspectorCalitate implements Serializable {

    @Id
    private UUID idInspector;

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
        this.idInspector = idInspector;
        this.fullName = fullName;
    }

    public void adaugaCertificare(Certificare c) {
        this.certificari.add(c);
    }

    public void declaraDisponibilitate(Perioada p) {
        this.disponibilitate.add(p);
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
}
