package ro.uaic.feaa.sdbis.supplychain.shared;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Entitate pentru stocarea documentelor fizice atasate (Facturi Scanate, NIR Semnat, Certificate Calitate).
 */
@Entity
@Table(name = "shared_documente_atasate")
public class DocumentAtasatEntity implements Serializable {
    private static final long serialVersionUID = 1L;


    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "tip_document", nullable = false, length = 50)
    private String tipDocument;

    @Column(name = "referinta_id", nullable = false)
    private UUID referintaId;

    @Column(name = "nume_fisier", nullable = false)
    private String numeFisier;

    @Column(name = "tip_continut", nullable = false, length = 100)
    private String tipContinut;

    @Column(name = "dimensiune_bytes", nullable = false)
    private long dimensiuneBytes;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "continut", columnDefinition = "BYTEA")
    private byte[] continut;

    @Column(name = "data_incarcare", nullable = false)
    private Instant dataIncarcare = Instant.now();

    protected DocumentAtasatEntity() {}

    public DocumentAtasatEntity(UUID id, String tipDocument, UUID referintaId, String numeFisier, String tipContinut, byte[] continut) {
        this.id = id;
        this.tipDocument = tipDocument;
        this.referintaId = referintaId;
        this.numeFisier = numeFisier;
        this.tipContinut = tipContinut;
        this.continut = continut;
        this.dimensiuneBytes = continut != null ? continut.length : 0;
        this.dataIncarcare = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getTipDocument() {
        return tipDocument;
    }

    public UUID getReferintaId() {
        return referintaId;
    }

    public String getNumeFisier() {
        return numeFisier;
    }

    public String getTipContinut() {
        return tipContinut;
    }

    public long getDimensiuneBytes() {
        return dimensiuneBytes;
    }

    public byte[] getContinut() {
        return continut;
    }

    public Instant getDataIncarcare() {
        return dataIncarcare;
    }
}

