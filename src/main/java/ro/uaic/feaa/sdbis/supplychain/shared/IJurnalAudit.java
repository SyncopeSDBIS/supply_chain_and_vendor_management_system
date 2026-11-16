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

    /**
     * Arhiveaza inregistrari vechi de audit din shared_audit_log in shared_audit_log_archive (cold-storage, OPS-01).
     * Ruleaza in batch-uri atomice pentru evitarea blocajelor de tabela la volum mare de date.
     */
    int arhiveazaLoguriVechi(java.time.Instant cutoff, int batchSize);
}
