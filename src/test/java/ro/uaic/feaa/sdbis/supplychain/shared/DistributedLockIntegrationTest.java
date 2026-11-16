package ro.uaic.feaa.sdbis.supplychain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DisplayName("Cluster & High-Availability - Distributed Locking Tests")
public class DistributedLockIntegrationTest {

    @Autowired
    private DistributedLockService lockService;

    @Test
    @DisplayName("Lock: Primul nod obtine lock-ul, iar al doilea apel concurent este respins")
    void testDistributedLockingMutualExclusion() {
        String lockKey = "job-calcul-scorecard-test";

        // 1. Primul nod cere lock pentru 1 minut
        boolean acquired = lockService.tryLock(lockKey, Duration.ofMinutes(1));
        assertTrue(acquired, "Primul apel trebuie sa obtina lock-ul cu succes");

        // 2. Al doilea apel pe aceeasi cheie trebuie sa fie respins (mutuala excluziune)
        boolean secondAttempt = lockService.tryLock(lockKey, Duration.ofMinutes(1));
        assertFalse(secondAttempt, "Al doilea apel concurent trebuie respins pentru a evita dubla executie");

        // 3. Eliberare lock
        lockService.releaseLock(lockKey);

        // 4. Dupa eliberare, lock-ul poate fi obtinut din nou
        boolean reacquired = lockService.tryLock(lockKey, Duration.ofMinutes(1));
        assertTrue(reacquired, "Dupa eliberare lock-ul trebuie sa fie din nou disponibil");

        lockService.releaseLock(lockKey);
    }
}

