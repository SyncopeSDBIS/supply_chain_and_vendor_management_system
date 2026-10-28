package ro.uaic.feaa.sdbis.supplychain.comenzi.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.EstimareLeadTimeService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.computation.ComandaComputationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.computation.IComandaComputationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.policy.PoliticaAprobareComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.validation.ComandaValidationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.validation.IComandaValidationService;

@Configuration
public class ComenziDomainConfig {

    @Bean
    public EstimareLeadTimeService estimareLeadTimeService() {
        return new EstimareLeadTimeService();
    }

    @Bean
    public IComandaValidationService comandaValidationService() {
        return new ComandaValidationService();
    }

    @Bean
    public IComandaComputationService comandaComputationService() {
        return new ComandaComputationService();
    }

    @Bean
    public PoliticaAprobareComanda politicaAprobareComanda() {
        return new PoliticaAprobareComanda();
    }
}
