package ro.uaic.feaa.sdbis.supplychain.shared;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TimeProvider & Deterministic Time-Travel Testing")
class TimeProviderTest {

    @AfterEach
    void tearDown() {
        TimeProvider.reset();
    }

    @Test
    @DisplayName("TimeProvider furnizeaza implicit fusul orar Europe/Bucharest")
    void testDefaultTimeProvider() {
        assertNotNull(TimeProvider.getClock());
        assertEquals(ZoneId.of("Europe/Bucharest"), TimeProvider.getClock().getZone());
        assertNotNull(TimeProvider.now());
        assertNotNull(TimeProvider.today());
    }

    @Test
    @DisplayName("TimeProvider permite time-travel determinist prin setClock")
    void testTimeTravelClock() {
        Instant fixedInstant = Instant.parse("2028-06-15T10:00:00Z");
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.of("Europe/Bucharest"));

        TimeProvider.setClock(fixedClock);

        assertEquals(fixedInstant, TimeProvider.now());
        assertEquals(LocalDate.of(2028, 6, 15), TimeProvider.today());

        Perioada p = Perioada.lunaCurenta();
        assertEquals(LocalDate.of(2028, 6, 1), p.getDataInceput());
        assertEquals(LocalDate.of(2028, 6, 30), p.getDataFinalizare());
    }
}

