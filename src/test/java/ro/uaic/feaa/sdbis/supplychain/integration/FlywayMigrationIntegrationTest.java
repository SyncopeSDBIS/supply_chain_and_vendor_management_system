package ro.uaic.feaa.sdbis.supplychain.integration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DisplayName("Integration Test: Validare Riguroasă Scripturi Migrare Flyway (V1..V15)")
public class FlywayMigrationIntegrationTest {

    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("Test Execuție Flyway: Toate scripturile de migrare V1..V15 se aplică cu succes pe schema SQL")
    void testAllFlywayMigrationsExecuteCleanly() {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();

        var result = flyway.migrate();
        assertTrue(result.success, "Migrarile Flyway trebuie sa fie executate cu succes fara nicio eroare de sintaxa sau constrangere");
    }
}

