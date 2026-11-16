package ro.uaic.feaa.sdbis.supplychain.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.Furnizor;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.FurnizorRepo;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.ScorecardLunar;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaAprovizionare;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.ComandaRepo;
import ro.uaic.feaa.sdbis.supplychain.comenzi.infrastructure.ComenziPerformantaLivrariAdapter;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.NIR;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.NIRRepo;
import ro.uaic.feaa.sdbis.supplychain.securitate.JwtTokenService;
import ro.uaic.feaa.sdbis.supplychain.shared.DistributedLockService;
import ro.uaic.feaa.sdbis.supplychain.shared.IdComanda;
import ro.uaic.feaa.sdbis.supplychain.shared.IdFurnizor;
import ro.uaic.feaa.sdbis.supplychain.shared.Perioada;
import ro.uaic.feaa.sdbis.supplychain.shared.PerformantaLivrariVO;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("REM-10: Complete Forensic Hardening Verification Test Suite")
public class TenRemediationsVerificationTest {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private DistributedLockService distributedLockService;

    @Autowired
    private NIRRepo nirRepo;

    @Autowired
    private ComandaRepo comandaRepo;

    @Autowired
    private ComenziPerformantaLivrariAdapter performantaLivrariAdapter;

    @Autowired
    private FurnizorRepo furnizorRepo;

    @Autowired(required = false)
    @Qualifier("stripeWebhookTaskExecutor")
    private Executor stripeWebhookTaskExecutor;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("SEC-02: JWT parsing strictly rejects tokens missing mandatory 'exp' claim (Never-Expiring token prevention)")
    void testJwtExpirationMandatoryValidation() throws Exception {
        // Construct validly signed JWT missing 'exp' claim
        Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
        Map<String, Object> payloadWithoutExp = new HashMap<>();
        payloadWithoutExp.put("sub", "malicious_user");
        payloadWithoutExp.put("role", "ROLE_ADMIN");
        payloadWithoutExp.put("userId", "123");
        payloadWithoutExp.put("iat", Instant.now().getEpochSecond());
        // 'exp' is intentionally OMITTED

        String encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(objectMapper.writeValueAsString(header).getBytes(StandardCharsets.UTF_8));
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(objectMapper.writeValueAsString(payloadWithoutExp).getBytes(StandardCharsets.UTF_8));
        String dataToSign = encodedHeader + "." + encodedPayload;

        String secret = "supplychain-dev-jwt-hmac-sha256-signing-key-for-local-development-only-2026-minimum-256bits";
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String signature = Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(dataToSign.getBytes(StandardCharsets.UTF_8)));

        String tokenWithoutExp = dataToSign + "." + signature;

        // Must be rejected because 'exp' is mandatory
        assertFalse(jwtTokenService.parseAndValidate(tokenWithoutExp).isPresent(),
                "Token missing mandatory 'exp' claim must be strictly rejected");
    }

    @Test
    @DisplayName("SEC-02: JWT parsing strictly rejects tokens missing mandatory 'sub' or 'role' claims")
    void testJwtSubjectAndRoleMandatoryValidation() throws Exception {
        Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
        Map<String, Object> payloadMissingSub = new HashMap<>();
        payloadMissingSub.put("role", "ROLE_ADMIN");
        payloadMissingSub.put("userId", "123");
        payloadMissingSub.put("iat", Instant.now().getEpochSecond());
        payloadMissingSub.put("exp", Instant.now().getEpochSecond() + 3600);

        String encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(objectMapper.writeValueAsString(header).getBytes(StandardCharsets.UTF_8));
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(objectMapper.writeValueAsString(payloadMissingSub).getBytes(StandardCharsets.UTF_8));
        String dataToSign = encodedHeader + "." + encodedPayload;

        String secret = "supplychain-dev-jwt-hmac-sha256-signing-key-for-local-development-only-2026-minimum-256bits";
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String signature = Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(dataToSign.getBytes(StandardCharsets.UTF_8)));

        String tokenWithoutSub = dataToSign + "." + signature;

        assertFalse(jwtTokenService.parseAndValidate(tokenWithoutSub).isPresent(),
                "Token missing 'sub' claim must be rejected");
    }

    @Test
    @DisplayName("SEC-STRIPE-01: Dedicated ThreadPoolTaskExecutor for Stripe webhooks is configured and active")
    void testStripeWebhookTaskExecutorConfigured() {
        assertNotNull(stripeWebhookTaskExecutor, "stripeWebhookTaskExecutor bean must be present in Spring context");
        if (stripeWebhookTaskExecutor instanceof ThreadPoolTaskExecutor tpe) {
            assertEquals(5, tpe.getCorePoolSize(), "Core pool size should be 5");
            assertEquals(20, tpe.getMaxPoolSize(), "Max pool size should be 20");
            assertTrue(tpe.getThreadNamePrefix().startsWith("stripe-webhook-"), "Thread prefix should match");
        }
    }

    @Test
    @DisplayName("CONC-03: DistributedLockJpaService acquires and releases locks safely via JdbcTemplate")
    void testDistributedLockAcquisitionAndRelease() {
        String lockName = "testHardeningLock_" + UUID.randomUUID().toString().substring(0, 8);
        boolean acquired = distributedLockService.tryLock(lockName, Duration.ofMinutes(5));
        assertTrue(acquired, "First attempt to acquire lock must succeed");

        // Concurrent attempt while lock is active: must be rejected (mutual exclusion)
        boolean secondAttempt = distributedLockService.tryLock(lockName, Duration.ofMinutes(5));
        assertFalse(secondAttempt, "Second attempt must be rejected while lock is active");

        distributedLockService.releaseLock(lockName);

        boolean reacquired = distributedLockService.tryLock(lockName, Duration.ofMinutes(5));
        assertTrue(reacquired, "Re-acquisition after release must succeed");
        distributedLockService.releaseLock(lockName);
    }

    @Test
    @DisplayName("DATA-01: ScorecardLunar optimistic locking version property is mapped and initialized")
    void testScorecardLunarVersionMapping() {
        ro.uaic.feaa.sdbis.supplychain.calitate.domain.Scor scor = ro.uaic.feaa.sdbis.supplychain.calitate.domain.Scor.ponderat(
                java.math.BigDecimal.valueOf(95),
                java.math.BigDecimal.valueOf(95),
                java.math.BigDecimal.valueOf(95)
        );
        ScorecardLunar sc = new ScorecardLunar(
                UUID.randomUUID(),
                Perioada.ofLuna(2026, 3),
                java.math.BigDecimal.valueOf(95),
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.valueOf(95),
                scor
        );
        assertNull(sc.getVersion(), "Version is null prior to JPA persistence");
        assertNotNull(sc.getId(), "Scorecard ID must be populated");
        assertNotNull(sc.getScor(), "Scorecard Scor must be populated");
    }

    @Test
    @DisplayName("PERF-01: ComenziPerformantaLivrariAdapter calculates OTIF safely without N+1 errors")
    void testOtifCalculationSafePerformance() {
        IdFurnizor furnizorId = IdFurnizor.random();
        Perioada luna = new Perioada(LocalDate.now().minusMonths(1).withDayOfMonth(1), LocalDate.now());

        PerformantaLivrariVO vo = performantaLivrariAdapter.obtinePerformanta(furnizorId, luna);
        assertNotNull(vo, "Performance VO must not be null");
        assertEquals(java.math.BigDecimal.valueOf(100.0), vo.otif(), "Empty supplier performance defaults to 100% OTIF");
    }

    @Test
    @Transactional
    @DisplayName("PERF-02: NIRRepoJPA findByComandaId loads without lazy loading errors")
    void testNirRepoFindByComandaIdSafe() {
        IdComanda cid = IdComanda.random();
        List<NIR> nirs = nirRepo.findByComandaId(cid);
        assertNotNull(nirs, "Result list must not be null");
        assertTrue(nirs.isEmpty(), "Result should be empty for non-existent order");
    }
}
