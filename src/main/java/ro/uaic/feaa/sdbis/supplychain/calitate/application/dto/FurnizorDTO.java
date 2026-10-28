package ro.uaic.feaa.sdbis.supplychain.calitate.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.xml.bind.annotation.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@XmlRootElement(name = "furnizor")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonRootName("furnizor")
public class FurnizorDTO implements Serializable {

    @XmlElement
    @JsonProperty("id")
    private UUID id;

    @XmlElement
    @JsonProperty("codFiscal")
    private String codFiscal;

    @XmlElement
    @JsonProperty("denumire")
    private String denumire;

    @XmlElement
    @JsonProperty("adresa")
    private String adresa;

    @XmlElement
    @JsonProperty("iban")
    private String iban;

    @XmlElement
    @JsonProperty("stareFurnizor")
    private String stareFurnizor;

    @XmlElementWrapper(name = "emailuri")
    @XmlElement(name = "email")
    @JsonProperty("emailuri")
    private Set<String> emailuri = new HashSet<>();

    @XmlElementWrapper(name = "categoriiMateriale")
    @XmlElement(name = "categorie")
    @JsonProperty("categoriiMateriale")
    private Set<String> categoriiMateriale = new HashSet<>();

    @XmlElement
    @JsonProperty("termenPlataZile")
    private Integer termenPlataZile;

    public FurnizorDTO() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCodFiscal() { return codFiscal; }
    public void setCodFiscal(String codFiscal) { this.codFiscal = codFiscal; }

    public String getDenumire() { return denumire; }
    public void setDenumire(String denumire) { this.denumire = denumire; }

    public String getAdresa() { return adresa; }
    public void setAdresa(String adresa) { this.adresa = adresa; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public String getStareFurnizor() { return stareFurnizor; }
    public void setStareFurnizor(String stareFurnizor) { this.stareFurnizor = stareFurnizor; }

    public Set<String> getEmailuri() { return emailuri; }
    public void setEmailuri(Set<String> emailuri) { this.emailuri = emailuri; }

    public Set<String> getCategoriiMateriale() { return categoriiMateriale; }
    public void setCategoriiMateriale(Set<String> categoriiMateriale) { this.categoriiMateriale = categoriiMateriale; }

    public Integer getTermenPlataZile() { return termenPlataZile; }
    public void setTermenPlataZile(Integer termenPlataZile) { this.termenPlataZile = termenPlataZile; }
}
