package ro.uaic.feaa.sdbis.supplychain.shared;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Furnizor de timp centralizat pentru suportul operatiunilor dependente de timp
 * si time-travel testing determinist.
 * Configureaza implicit fusul orar Europe/Bucharest (EET/EEST) pentru a preveni discrepantele
 * intre containerele configurate pe UTC si serverele de aplicatie/baza de date.
 */
public final class TimeProvider {

    public static final ZoneId DEFAULT_ZONE = ZoneId.of("Europe/Bucharest");
    private static volatile Clock clock = Clock.system(DEFAULT_ZONE);

    private TimeProvider() {}

    public static Clock getClock() {
        return clock;
    }

    public static void setClock(Clock newClock) {
        clock = newClock != null ? newClock : Clock.system(DEFAULT_ZONE);
    }

    public static void reset() {
        clock = Clock.system(DEFAULT_ZONE);
    }

    public static Instant now() {
        return clock.instant();
    }

    public static LocalDate today() {
        return LocalDate.now(clock);
    }
}

