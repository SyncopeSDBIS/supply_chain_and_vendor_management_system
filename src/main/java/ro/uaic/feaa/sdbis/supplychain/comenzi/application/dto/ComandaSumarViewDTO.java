package ro.uaic.feaa.sdbis.supplychain.comenzi.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Published Language DTO conform Figurilor 14 si 19 din Project_OOAD.docx.
 * Suporta dubla serializare JSON (Jackson) si XML (JAXB).
 */
@XmlRootElement(name = "comandaSummary")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonRootName("comandaSummary")
public class ComandaSumarViewDTO implements Serializable {
    private static final long serialVersionUID = 1L;


    @XmlElement(name = "comandaId")
    @JsonProperty("comandaId")
    private UUID comandaId;

    @XmlElementWrapper(name = "linii")
    @XmlElement(name = "linie")
    @JsonProperty("linii")
    @SuppressWarnings("serial") // Collection interface List does not implement Serializable directly; runtime instance is ArrayList
    private List<LinieSumarDTO> linii = new ArrayList<>();

    public ComandaSumarViewDTO() {}

    public ComandaSumarViewDTO(UUID comandaId, List<LinieSumarDTO> linii) {
        this.comandaId = comandaId;
        if (linii != null) {
            this.linii = linii;
        }
    }

    public UUID getComandaId() { return comandaId; }
    public void setComandaId(UUID comandaId) { this.comandaId = comandaId; }

    public List<LinieSumarDTO> getLinii() { return linii; }
    public void setLinii(List<LinieSumarDTO> linii) { this.linii = linii; }
}

