package ro.uaic.feaa.sdbis.supplychain.shared;

/**
 * Port pentru jurnalizarea imutabila a accesului si actiunilor critice (GDPR, NIS2, preturi, plati).
 */
public interface IJurnalAudit {
    void inregistreazaAcces(String actiune, String obiect);
}
