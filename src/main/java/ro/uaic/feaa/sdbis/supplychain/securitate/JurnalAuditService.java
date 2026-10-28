package ro.uaic.feaa.sdbis.supplychain.securitate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.shared.AuditLogEntry;
import ro.uaic.feaa.sdbis.supplychain.shared.IJurnalAudit;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.UtilityService;

import java.time.Instant;
import java.util.UUID;

/**
 * Serviciu pentru jurnalizarea imutabila (append-only) a actiunilor de securitate si financiare (GDPR / NIS2)
 * si trasabilitatea schimbarilor operationale (Sprint 2 - ADR-003).
 */
@Service
@UtilityService
public class JurnalAuditService implements IJurnalAudit {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void inregistreazaAcces(String actiune, String obiect) {
        AuditLogEntry entry = new AuditLogEntry(UUID.randomUUID(), Instant.now(), actiune, obiect);
        em.persist(entry);
        System.out.println("[AUDIT LOG] " + actiune + " | " + obiect);
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
}
