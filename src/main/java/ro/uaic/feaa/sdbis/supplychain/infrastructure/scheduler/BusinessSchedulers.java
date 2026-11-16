package ro.uaic.feaa.sdbis.supplychain.infrastructure.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.calitate.api.ICalitateModuleApi;
import ro.uaic.feaa.sdbis.supplychain.comenzi.api.IComenziModuleApi;
import ro.uaic.feaa.sdbis.supplychain.facturi.api.IFacturiModuleApi;
import ro.uaic.feaa.sdbis.supplychain.shared.DistributedLockService;
import ro.uaic.feaa.sdbis.supplychain.shared.InboxRepo;
import ro.uaic.feaa.sdbis.supplychain.shared.OutboxRepo;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Cele 4 Schedulere de Business conforme cu Tabelul 1.1 si Sectiunea 8 din Master Blueprint:
 * 1. ComandaConfirmareTimeout (48h)
 * 2. CalculCalificativLunar (Ultima zi a lunii)
 * 3. ExpirareCertificariInspectori (Zilnic la 08:00)
 * 4. VerificareScadenteFacturi (Zilnic la 08:30)
 * Plus scheduler de mentenanta si retention pentru Transactional Outbox / Inbox.
 *
 * Securizat cu Distributed Locking la nivel de baza de date pentru scalare in cluster.
 */
@Component
public class BusinessSchedulers {

    private static final Logger log = LoggerFactory.getLogger(BusinessSchedulers.class);

    private final IComenziModuleApi comenziApi;
    private final ICalitateModuleApi calitateApi;
    private final IFacturiModuleApi facturiApi;
    private final OutboxRepo outboxRepo;
    private final InboxRepo inboxRepo;
    private final DistributedLockService distributedLockService;
    private final ro.uaic.feaa.sdbis.supplychain.shared.IJurnalAudit jurnalAudit;

    public BusinessSchedulers(IComenziModuleApi comenziApi,
                              ICalitateModuleApi calitateApi,
                              IFacturiModuleApi facturiApi,
                              OutboxRepo outboxRepo,
                              InboxRepo inboxRepo,
                              DistributedLockService distributedLockService) {
        this(comenziApi, calitateApi, facturiApi, outboxRepo, inboxRepo, distributedLockService, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public BusinessSchedulers(IComenziModuleApi comenziApi,
                              ICalitateModuleApi calitateApi,
                              IFacturiModuleApi facturiApi,
                              OutboxRepo outboxRepo,
                              InboxRepo inboxRepo,
                              DistributedLockService distributedLockService,
                              @org.springframework.beans.factory.annotation.Autowired(required = false)
                              ro.uaic.feaa.sdbis.supplychain.shared.IJurnalAudit jurnalAudit) {
        this.comenziApi = comenziApi;
        this.calitateApi = calitateApi;
        this.facturiApi = facturiApi;
        this.outboxRepo = outboxRepo;
        this.inboxRepo = inboxRepo;
        this.distributedLockService = distributedLockService;
        this.jurnalAudit = jurnalAudit;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void verificaTimeoutConfirmareComenzi() {
        if (!distributedLockService.tryLock("verificaTimeoutConfirmareComenzi", Duration.ofMinutes(15))) {
            log.info("[Scheduler] Job 'verificaTimeoutConfirmareComenzi' deja rulat pe alt nod.");
            return;
        }
        try {
            comenziApi.proceseazaTimeoutConfirmareComenzi();
        } finally {
            distributedLockService.releaseLock("verificaTimeoutConfirmareComenzi");
        }
    }

    @Scheduled(cron = "0 0 23 L * *")
    public void calculScorecardLunarAutomat() {
        if (!distributedLockService.tryLock("calculScorecardLunarAutomat", Duration.ofHours(2))) {
            log.info("[Scheduler] Job 'calculScorecardLunarAutomat' deja rulat pe alt nod.");
            return;
        }
        try {
            calitateApi.proceseazaCalculScorecardLunarAutomat();
        } finally {
            distributedLockService.releaseLock("calculScorecardLunarAutomat");
        }
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void verificaExpirareCertificari() {
        if (!distributedLockService.tryLock("verificaExpirareCertificari", Duration.ofHours(1))) {
            log.info("[Scheduler] Job 'verificaExpirareCertificari' deja rulat pe alt nod.");
            return;
        }
        try {
            calitateApi.proceseazaExpirareCertificariInspectori();
        } finally {
            distributedLockService.releaseLock("verificaExpirareCertificari");
        }
    }

    @Scheduled(cron = "0 30 8 * * *")
    public void verificaScadenteFacturi() {
        if (!distributedLockService.tryLock("verificaScadenteFacturi", Duration.ofHours(1))) {
            log.info("[Scheduler] Job 'verificaScadenteFacturi' deja rulat pe alt nod.");
            return;
        }
        try {
            facturiApi.proceseazaVerificareScadenteFacturi();
        } finally {
            distributedLockService.releaseLock("verificaScadenteFacturi");
        }
    }

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void purjareEvenimenteProcesateOutboxSiInbox() {
        if (!distributedLockService.tryLock("purjareEvenimenteProcesateOutboxSiInbox", Duration.ofHours(1))) {
            log.info("[Scheduler] Job 'purjareEvenimenteProcesateOutboxSiInbox' deja rulat pe alt nod.");
            return;
        }
        try {
            Instant cutoff = Instant.now().minus(30, ChronoUnit.DAYS);
            int outboxPurged = outboxRepo.purgePublishedEventsOlderThan(cutoff);
            int inboxPurged = inboxRepo.purgeProcessedEventsOlderThan(cutoff);
            log.info("[Retention Purge] Outbox purge: {} evenimente, Inbox purge: {} evenimente mai vechi de 30 de zile.",
                outboxPurged, inboxPurged);
        } finally {
            distributedLockService.releaseLock("purjareEvenimenteProcesateOutboxSiInbox");
        }
    }

    /**
     * Job zilnic la 03:00 pentru arhivarea inregistrarilor de audit mai vechi de 90 de zile in cold-storage (OPS-01).
     * Previne degradarea performantei pe tabela activa shared_audit_log prin mutare in shared_audit_log_archive.
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void archiveOldAuditLogs() {
        if (!distributedLockService.tryLock("archiveOldAuditLogs", Duration.ofHours(1))) {
            log.info("[Scheduler] Job 'archiveOldAuditLogs' deja rulat pe alt nod.");
            return;
        }
        try {
            if (jurnalAudit == null) {
                return;
            }
            Instant cutoff = Instant.now().minus(90, ChronoUnit.DAYS);
            int totalArchived = 0;
            int batch;
            do {
                batch = jurnalAudit.arhiveazaLoguriVechi(cutoff, 500);
                totalArchived += batch;
            } while (batch == 500);
            log.info("[Retention Audit Archival] Arhivat cu succes {} inregistrari de audit vechi (>90 zile) in cold storage.", totalArchived);
        } finally {
            distributedLockService.releaseLock("archiveOldAuditLogs");
        }
    }
}
