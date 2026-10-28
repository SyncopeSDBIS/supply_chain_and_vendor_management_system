package ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.ReconciliereService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.computation.IReconciliereComputationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.computation.ReconciliereComputationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.policy.PoliticaAutorizarePlata;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.validation.FacturaValidationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.validation.IFacturaValidationService;

@Configuration
public class FacturiDomainConfig {

    @Bean
    public ReconciliereService reconciliereService() {
        return new ReconciliereService();
    }

    @Bean
    public IFacturaValidationService facturaValidationService() {
        return new FacturaValidationService();
    }

    @Bean
    public IReconciliereComputationService reconciliereComputationService() {
        return new ReconciliereComputationService();
    }

    @Bean
    public PoliticaAutorizarePlata politicaAutorizarePlata() {
        return new PoliticaAutorizarePlata();
    }
}
