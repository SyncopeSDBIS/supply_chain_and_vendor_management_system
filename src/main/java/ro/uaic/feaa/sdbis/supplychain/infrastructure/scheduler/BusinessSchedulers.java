package ro.uaic.feaa.sdbis.supplychain.infrastructure.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ro.uaic.feaa.sdbis.supplychain.calitate.api.ICalitateModuleApi;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.IComenziModuleApi;
import ro.uaic.feaa.sdbis.supplychain.facturi.api.IFacturiModuleApi;

/**
 * Cele 4 Schedulere de Business conforme cu Tabelul 1.1 si Sectiunea 8 din Master Blueprint:
 * 1. ComandaConfirmareTimeout (48h)
 * 2. CalculCalificativLunar (Ultima zi a lunii)
 * 3. ExpirareCertificariInspectori (Zilnic la 08:00)
 * 4. VerificareScadenteFacturi (Zilnic la 08:30)
 *
 * Respecta principiul Clean Architecture si limitele Spring Modulith:
 * Actioneaza ca un scheduler orchestrator delegand catre API-urile publice ale modulelor.
 */
@Component
public class BusinessSchedulers {

    private final IComenziModuleApi comenziApi;
    private final ICalitateModuleApi calitateApi;
    private final IFacturiModuleApi facturiApi;

    public BusinessSchedulers(IComenziModuleApi comenziApi,
                              ICalitateModuleApi calitateApi,
                              IFacturiModuleApi facturiApi) {
        this.comenziApi = comenziApi;
        this.calitateApi = calitateApi;
        this.facturiApi = facturiApi;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void verificaTimeoutConfirmareComenzi() {
        comenziApi.proceseazaTimeoutConfirmareComenzi();
    }

    @Scheduled(cron = "0 0 23 L * *")
    public void calculScorecardLunarAutomat() {
        calitateApi.proceseazaCalculScorecardLunarAutomat();
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void verificaExpirareCertificari() {
        calitateApi.proceseazaExpirareCertificariInspectori();
    }

    @Scheduled(cron = "0 30 8 * * *")
    public void verificaScadenteFacturi() {
        facturiApi.proceseazaVerificareScadenteFacturi();
    }
}
