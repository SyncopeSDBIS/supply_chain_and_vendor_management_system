package ro.uaic.feaa.sdbis.supplychain.calitate.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.AlocareInspectorService;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.CalculScorecardService;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.policy.PoliticaSelectieAQL;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.validation.CalitateValidationService;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.validation.ICalitateValidationService;

@Configuration
public class CalitateDomainConfig {

    @Bean
    public CalculScorecardService calculScorecardService() {
        return new CalculScorecardService();
    }

    @Bean
    public AlocareInspectorService alocareInspectorService() {
        return new AlocareInspectorService();
    }

    @Bean
    public ICalitateValidationService calitateValidationService() {
        return new CalitateValidationService();
    }

    @Bean
    public PoliticaSelectieAQL politicaSelectieAQL() {
        return new PoliticaSelectieAQL();
    }
}
