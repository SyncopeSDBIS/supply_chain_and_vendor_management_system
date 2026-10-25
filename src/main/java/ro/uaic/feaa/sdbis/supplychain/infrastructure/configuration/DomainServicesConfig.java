package ro.uaic.feaa.sdbis.supplychain.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.AlocareInspectorService;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.CalculScorecardService;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.computation.ICalculScorecardService;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.policy.PoliticaSelectieAQL;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.validation.CalitateValidationService;
import ro.uaic.feaa.sdbis.supplychain.calitate.domain.validation.ICalitateValidationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.EstimareLeadTimeService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.computation.ComandaComputationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.computation.IComandaComputationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.policy.PoliticaAprobareComanda;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.validation.ComandaValidationService;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.validation.IComandaValidationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.ReconciliereService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.computation.IReconciliereComputationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.computation.ReconciliereComputationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.policy.PoliticaAutorizarePlata;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.validation.FacturaValidationService;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.validation.IFacturaValidationService;

/**
 * Configureaza Domain Services, Validation Services si Computation Services ca bean-uri Spring,
 * respectand cu strictete izolarea pura de domeniu (ADR-003, ADR-011).
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

    // --- Sprint 2 Validation Services ---

    @Bean
    public IComandaValidationService comandaValidationService() {
        return new ComandaValidationService();
    }

    @Bean
    public ICalitateValidationService calitateValidationService() {
        return new CalitateValidationService();
    }

    @Bean
    public IFacturaValidationService facturaValidationService() {
        return new FacturaValidationService();
    }

    // --- Sprint 2 Computation Services ---

    @Bean
    public IComandaComputationService comandaComputationService() {
        return new ComandaComputationService();
    }

    @Bean
    public IReconciliereComputationService reconciliereComputationService() {
        return new ReconciliereComputationService();
    }

    // --- Sprint 2 Business Policies (Other Business Rules) ---

    @Bean
    public PoliticaAprobareComanda politicaAprobareComanda() {
        return new PoliticaAprobareComanda();
    }

    @Bean
    public PoliticaSelectieAQL politicaSelectieAQL() {
        return new PoliticaSelectieAQL();
    }

    @Bean
    public PoliticaAutorizarePlata politicaAutorizarePlata() {
        return new PoliticaAutorizarePlata();
    }
}
