package ro.uaic.feaa.sdbis.supplychain.shared;

/**
 * Port pentru jurnalizarea imutabila (append-only) a accesului, actiunilor critice
 * si trasabilitatii modificarilor operationale (Sprint 2 - ADR-003, GDPR, NIS2).
 */
public interface IJurnalAudit {

    /**
     * Jurnalizeaza un acces sau o actiune importanta.
     */
    void inregistreazaAcces(String actiune, String obiect);

    /**
     * Asigura trasabilitatea tranzitiilor de stare si modificarilor operationale (Audit Service).
     */
    void inregistreazaModificare(String entitate, String id, String stareVeche, String stareNoua);

    /**
     * Jurnalizeaza evenimente operationale de business pentru auditabilitate.
     */
    void inregistreazaEvenimentOperational(String categorie, String referinta, String detalii);
}
