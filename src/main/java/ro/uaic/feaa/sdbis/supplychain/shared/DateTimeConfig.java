package ro.uaic.feaa.sdbis.supplychain.shared;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Configuratie Spring pentru injectarea instantei de java.time.Clock
 * in toate serviciile si componentele dependente de timp.
 */
@Configuration
public class DateTimeConfig {

    @Bean
    @ConditionalOnMissingBean
    public Clock clock() {
        return TimeProvider.getClock();
    }
}

