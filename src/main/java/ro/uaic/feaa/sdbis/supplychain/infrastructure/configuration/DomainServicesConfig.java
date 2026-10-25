package ro.uaic.feaa.sdbis.supplychain.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.AlocareInspectorService;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.CalculScorecardService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.EstimareLeadTimeService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.ReconciliereService;

/**
 * Configureaza Domain Services ca bean-uri Spring in Infrastructure Layer,
 * mentinand Domain Layer-ul 100% curat si liber de dependente de Spring (ADR-011).
 */
@Configuration
public class DomainServicesConfig {

    @Bean
    public EstimareLeadTimeService estimareLeadTimeService() {
        return new EstimareLeadTimeService();
    }

    @Bean
    public CalculScorecardService calculScorecardService() {
        return new CalculScorecardService();
    }

    @Bean
    public AlocareInspectorService alocareInspectorService() {
        return new AlocareInspectorService();
    }

    @Bean
    public ReconciliereService reconciliereService() {
        return new ReconciliereService();
    }
}
