package ro.uaic.feaa.sdbis.supplychain.securitate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.shared.AuditLogArchiveEntry;
import ro.uaic.feaa.sdbis.supplychain.shared.AuditLogEntry;
import ro.uaic.feaa.sdbis.supplychain.shared.IJurnalAudit;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.UtilityService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Serviciu pentru jurnalizarea imutabila (append-only) a actiunilor de securitate si financiare (GDPR / NIS2),
 * trasabilitatea schimbarilor operationale si mentenanta prin arhivare periodica in cold-storage (OPS-01).
 */
@Service
@UtilityService
public class JurnalAuditService implements IJurnalAudit {

    private static final Logger log = LoggerFactory.getLogger(JurnalAuditService.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void inregistreazaAcces(String actiune, String obiect) {
        AuditLogEntry entry = new AuditLogEntry(UUID.randomUUID(), Instant.now(), actiune, obiect);
        em.persist(entry);
        log.info("[AUDIT LOG] {} | {}", actiune, obiect);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void inregistreazaModificare(String entitate, String id, String stareVeche, String stareNoua) {
        String actiune = "MODIFICARE_STARE_" + (entitate != null ? entitate.toUpperCase() : "ENTITATE");
        String obiect = "Entitate: " + entitate + " [ID=" + id + "] StareVeche: " + stareVeche + " -> StareNoua: " + stareNoua;
        inregistreazaAcces(actiune, obiect);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void inregistreazaEvenimentOperational(String categorie, String referinta, String detalii) {
        String actiune = "OPERATIONAL_" + (categorie != null ? categorie.toUpperCase() : "GENERAL");
        String obiect = "Ref: " + referinta + " | " + detalii;
        inregistreazaAcces(actiune, obiect);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int arhiveazaLoguriVechi(Instant cutoff, int batchSize) {
        int limit = batchSize > 0 ? batchSize : 500;
        List<AuditLogEntry> vechi = em.createQuery(
                "SELECT a FROM AuditLogEntry a WHERE a.timestamp < :cutoff ORDER BY a.timestamp ASC", AuditLogEntry.class)
                .setParameter("cutoff", cutoff)
                .setMaxResults(limit)
                .getResultList();

        if (vechi.isEmpty()) {
            return 0;
        }

        for (AuditLogEntry e : vechi) {
            AuditLogArchiveEntry archiveEntry = new AuditLogArchiveEntry(
                    e.getId(), e.getTimestamp(), e.getActiune(), e.getObiect());
            em.persist(archiveEntry);
            em.remove(e);
        }
        em.flush();
        log.info("[Retention Audit Archival] Transferat batch de {} inregistrari in shared_audit_log_archive.", vechi.size());
        return vechi.size();
    }
}
