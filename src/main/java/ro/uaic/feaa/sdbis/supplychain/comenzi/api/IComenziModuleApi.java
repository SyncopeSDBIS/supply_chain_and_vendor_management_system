package ro.uaic.feaa.sdbis.supplychain.comenzi.api;

import ro.uaic.feaa.sdbis.supplychain.comenzi.api.command.CreeazaComandaPublicCommand;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.dto.ComandaSummaryDTO;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.dto.PerformantaLivrariDTO;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Public Contract & Entry Point for the Comenzi (Procurement Orders) Bounded Context.
 * Other modules must interact with Comenzi EXCLUSIVELY through this interface.
 */
public interface IComenziModuleApi {

    UUID creeazaComanda(CreeazaComandaPublicCommand cmd);

    void confirmaComanda(UUID comandaId, LocalDate dataLivrareConfirmata);

    void anuleazaComanda(UUID comandaId, String motiv);

    Optional<ComandaSummaryDTO> obtineDetaliiComanda(UUID comandaId);

    boolean esteComandaEligibilaPentruReconciliere(UUID comandaId);

    boolean esteComandaInStare(UUID comandaId, String stare);
    
    int proceseazaTimeoutConfirmareComenzi();

    PerformantaLivrariDTO obtinePerformantaLivrari(UUID furnizorId, LocalDate dataInceput, LocalDate dataFinalizare);
}
