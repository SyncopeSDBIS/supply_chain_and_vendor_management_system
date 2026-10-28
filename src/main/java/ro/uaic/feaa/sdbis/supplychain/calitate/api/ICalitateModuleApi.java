package ro.uaic.feaa.sdbis.supplychain.calitate.api;

import ro.uaic.feaa.sdbis.supplychain.calitate.api.dto.FurnizorSummaryDTO;

import java.util.Optional;
import java.util.UUID;

/**
 * Public Contract & Entry Point for the Calitate & Gestiune Furnizori Bounded Context.
 * Other modules (Comenzi, Facturi) must interact with Calitate EXCLUSIVELY through this interface.
 */
public interface ICalitateModuleApi {

    boolean esteFurnizorEligibilPentruComenzi(UUID furnizorId);

    boolean esteFurnizorBlocat(UUID furnizorId);

    Optional<FurnizorSummaryDTO> obtineDetaliiFurnizor(UUID furnizorId);

    void blocheazaFurnizor(UUID furnizorId, String motiv);

    int proceseazaCalculScorecardLunarAutomat();

    int proceseazaExpirareCertificariInspectori();

    UUID programeazaInspectie(UUID lotId, UUID comandaId, String codMaterial);
}
