package ro.uaic.feaa.sdbis.supplychain.securitate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.shared.AuditLogEntry;
import ro.uaic.feaa.sdbis.supplychain.shared.IJurnalAudit;

import java.time.Instant;
import java.util.UUID;

/**
 * Serviciu pentru jurnalizarea imutabila (append-only) a actiunilor de securitate si financiare (GDPR / NIS2).
 */
@Service
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
}
