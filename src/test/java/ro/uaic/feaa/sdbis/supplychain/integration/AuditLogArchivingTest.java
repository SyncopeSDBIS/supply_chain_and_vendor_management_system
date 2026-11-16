package ro.uaic.feaa.sdbis.supplychain.integration;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.infrastructure.scheduler.BusinessSchedulers;
import ro.uaic.feaa.sdbis.supplychain.shared.AuditLogArchiveEntry;
import ro.uaic.feaa.sdbis.supplychain.shared.AuditLogEntry;
import ro.uaic.feaa.sdbis.supplychain.shared.IJurnalAudit;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("OPS-01: Audit Log Cold-Storage Archival & Maintenance Integration Tests")
public class AuditLogArchivingTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private BusinessSchedulers schedulers;

    @Autowired
    private IJurnalAudit jurnalAudit;

    @Test
    @DisplayName("OPS-01: Arhivarea automata muta inregistrarile vechi (>90 zile) in shared_audit_log_archive")
    void testAuditLogArchivingMaintenance() {
        UUID oldId1 = UUID.randomUUID();
        UUID oldId2 = UUID.randomUUID();
        UUID recentId = UUID.randomUUID();

        Instant oldTimestamp1 = Instant.now().minus(120, ChronoUnit.DAYS);
        Instant oldTimestamp2 = Instant.now().minus(95, ChronoUnit.DAYS);
        Instant recentTimestamp = Instant.now().minus(5, ChronoUnit.DAYS);

        // Insereaza 2 intrari vechi (>90 zile) si 1 intrare recenta (<90 zile)
        AuditLogEntry oldEntry1 = new AuditLogEntry(oldId1, oldTimestamp1, "LOGIN_LEGACY", "User 1 login 120 days ago");
        AuditLogEntry oldEntry2 = new AuditLogEntry(oldId2, oldTimestamp2, "PURCHASE_LEGACY", "Order approved 95 days ago");
        AuditLogEntry recentEntry = new AuditLogEntry(recentId, recentTimestamp, "LOGIN_RECENT", "User 1 login 5 days ago");

        em.persist(oldEntry1);
        em.persist(oldEntry2);
        em.persist(recentEntry);
        em.flush();

        // Executa arhivarea
        schedulers.archiveOldAuditLogs();
        em.flush();
        em.clear();

        // 1. Verifica ca intrarile vechi au fost eliminate din tabela activa shared_audit_log
        AuditLogEntry checkOld1 = em.find(AuditLogEntry.class, oldId1);
        AuditLogEntry checkOld2 = em.find(AuditLogEntry.class, oldId2);
        assertNull(checkOld1, "Intrarea veche 1 trebuie sa fie eliminata din tabela activa shared_audit_log.");
        assertNull(checkOld2, "Intrarea veche 2 trebuie sa fie eliminata din tabela activa shared_audit_log.");

        // 2. Verifica ca intrarea recenta a ramas intacta in tabela activa
        AuditLogEntry checkRecent = em.find(AuditLogEntry.class, recentId);
        assertNotNull(checkRecent, "Intrarea recenta (< 90 zile) trebuie sa ramana in tabela activa.");

        // 3. Verifica ca intrarile vechi se regasesc in tabela de arhiva shared_audit_log_archive
        AuditLogArchiveEntry archive1 = em.find(AuditLogArchiveEntry.class, oldId1);
        AuditLogArchiveEntry archive2 = em.find(AuditLogArchiveEntry.class, oldId2);
        assertNotNull(archive1, "Intrarea veche 1 trebuie sa fie prezenta in shared_audit_log_archive.");
        assertNotNull(archive2, "Intrarea veche 2 trebuie sa fie prezenta in shared_audit_log_archive.");

        assertEquals("LOGIN_LEGACY", archive1.getActiune());
        assertEquals("PURCHASE_LEGACY", archive2.getActiune());
    }
}
