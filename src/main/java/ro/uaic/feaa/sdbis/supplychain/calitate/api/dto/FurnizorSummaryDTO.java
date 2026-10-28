package ro.uaic.feaa.sdbis.supplychain.calitate.api.dto;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

/**
 * Public Contract DTO exposed by the Calitate / Vendor module.
 */
public record FurnizorSummaryDTO(
        UUID idFurnizor,
        String codFiscal,
        String denumire,
        String stareFurnizor,
        String iban,
        Set<String> categoriiMateriale,
        boolean esteEligibilPentruComenzi,
        boolean esteBlocat
) implements Serializable {}
