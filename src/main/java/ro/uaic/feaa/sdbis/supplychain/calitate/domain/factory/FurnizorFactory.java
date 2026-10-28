package ro.uaic.feaa.sdbis.supplychain.calitate.domain.factory;

import ro.uaic.feaa.sdbis.supplychain.calitate.domain.*;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.DomainFactory;
import ro.uaic.feaa.sdbis.supplychain.shared.exception.DomainValidationException;

import java.util.Objects;
import java.util.Set;

/**
 * Aggregate Factory pentru agregatul Furnizor (Sprint 2 - ADR-003).
 * Garanteaza initializarea consistenta a agregatului in starea initiala Candidat.
 */
@DomainFactory
public class FurnizorFactory {

    public static Furnizor creeaza(String codFiscal,
                                   String denumire,
                                   Adresa adresa,
                                   Set<Email> emailuri,
                                   Iban iban,
                                   Set<String> categoriiMateriale,
                                   ConditiiContractuale conditii) {
        if (codFiscal == null || codFiscal.trim().isEmpty()) {
            throw new DomainValidationException("COD_FISCAL_INVALID", "Codul fiscal al furnizorului este obligatoriu!");
        }
        if (denumire == null || denumire.trim().isEmpty()) {
            throw new DomainValidationException("DENUMIRE_INVALIDA", "Denumirea furnizorului este obligatorie!");
        }
        if (iban == null) {
            throw new DomainValidationException("IBAN_LIPSA", "Contul IBAN este obligatoriu pentru inregistrarea furnizorului!");
        }
        if (categoriiMateriale == null || categoriiMateriale.isEmpty()) {
            throw new DomainValidationException("CATEGORII_LIPSA", "Furnizorul trebuie sa aiba cel putin o categorie de materiale asociata!");
        }

        IdFurnizor id = IdFurnizor.random();
        return new Furnizor(id, codFiscal.trim(), denumire.trim(), adresa, emailuri, iban, categoriiMateriale, conditii);
    }
}
