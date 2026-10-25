package ro.uaic.feaa.sdbis.supplychain.shared.exception;

/**
 * Exceptie pentru incalcarea regulilor de validare a domeniului (ADR-003).
 */
public class DomainValidationException extends RuntimeException {

    private final String codEroare;

    public DomainValidationException(String mesaj) {
        super(mesaj);
        this.codEroare = "VALIDARE_DOMENIU";
    }

    public DomainValidationException(String codEroare, String mesaj) {
        super(mesaj);
        this.codEroare = codEroare;
    }

    public String getCodEroare() {
        return codEroare;
    }
}
