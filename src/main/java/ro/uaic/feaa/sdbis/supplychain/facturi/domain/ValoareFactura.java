package ro.uaic.feaa.sdbis.supplychain.facturi.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Embeddable
public class ValoareFactura implements Serializable {
    private BigDecimal valoareFaraTVA;
    private BigDecimal valoareTVA;
    private BigDecimal valoareTotala;

    protected ValoareFactura() {}

    public ValoareFactura(BigDecimal valoareFaraTVA, BigDecimal valoareTVA, BigDecimal valoareTotala) {
        this.valoareFaraTVA = valoareFaraTVA.setScale(2, RoundingMode.HALF_UP);
        this.valoareTVA = valoareTVA.setScale(2, RoundingMode.HALF_UP);
        this.valoareTotala = valoareTotala.setScale(2, RoundingMode.HALF_UP);
    }

    public static ValoareFactura calculeaza(List<LinieFactura> linii) {
        BigDecimal faraTVA = BigDecimal.ZERO;
        BigDecimal tva = BigDecimal.ZERO;
        if (linii != null) {
            for (LinieFactura l : linii) {
                faraTVA = faraTVA.add(l.getValoareFaraTVA());
                tva = tva.add(l.getValoareTVA());
            }
        }
        return new ValoareFactura(faraTVA, tva, faraTVA.add(tva));
    }

    public BigDecimal getValoareFaraTVA() { return valoareFaraTVA; }
    public BigDecimal getValoareTVA() { return valoareTVA; }
    public BigDecimal getValoareTotala() { return valoareTotala; }
}
