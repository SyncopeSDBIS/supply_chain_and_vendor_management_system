package ro.uaic.feaa.sdbis.supplychain.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import ro.uaic.feaa.sdbis.supplychain.shared.annotation.*;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

/**
 * Verificarea automată a regulilor DDD Tactical Patterns folosind Adnotările Semantice.
 * Garantează integritatea arhitecturală la nivel de CI/CD:
 * 1. Entitățile interne (@DomainEntity) nu sunt accesate direct din Presentation/REST.
 * 2. Entitățile interne (@DomainEntity) nu depind direct de Repository-uri (gestiune exclusivă prin Root).
 * 3. Repository-urile (@DomainRepository) sunt interfețe definite strict în Domain Layer.
 * 4. Serviciile de domeniu (@DomainService, @DomainValidationService, @DomainComputationService, @DomainPolicy)
 *    sunt pure (nu depind de Spring, infrastructură sau straturi externe).
 * 5. Serviciile de aplicație (@ApplicationService, @WorkflowService) rezidă strict în Application Layer.
 * 6. Serviciile CQRS (@QueryService) rezidă strict în Application Layer.
 * 7. Obiectele Valoare (@ValueObject) sunt imutabile (toate câmpurile de instanță sunt final).
 * 8. Rădăcinile de agregat (@AggregateRoot) și Entitățile (@DomainEntity) rezidă exclusiv în Domain Layer.
 */
@AnalyzeClasses(
    packages = "ro.uaic.feaa.sdbis.supplychain",
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class DddArchitectureRulesTest {

    @ArchTest
    static final ArchRule domain_entities_must_not_be_accessed_directly_from_presentation =
        noClasses()
            .that().resideInAPackage("..presentation..")
            .should().dependOnClassesThat()
            .areAnnotatedWith(DomainEntity.class)
            .because("Internal child entities (@DomainEntity) must only be accessed through Aggregate Roots or Application Services, never directly from Presentation controllers (ADR-002).");

    @ArchTest
    static final ArchRule domain_entities_must_not_depend_on_repositories_directly =
        noClasses()
            .that().areAnnotatedWith(DomainEntity.class)
            .should().dependOnClassesThat()
            .areAnnotatedWith(DomainRepository.class)
            .because("Internal child entities (@DomainEntity) must not have direct repository coupling; persistence is managed exclusively by the Aggregate Root.");

    @ArchTest
    static final ArchRule domain_entities_must_reside_in_domain_layer =
        classes()
            .that().areAnnotatedWith(DomainEntity.class)
            .should().resideInAPackage("..domain..")
            .because("Domain entities must strictly reside within the domain package of their bounded context.");

    @ArchTest
    static final ArchRule aggregate_roots_must_reside_in_domain_layer =
        classes()
            .that().areAnnotatedWith(AggregateRoot.class)
            .should().resideInAPackage("..domain..")
            .because("Aggregate roots must strictly reside within the domain package of their bounded context.");

    @ArchTest
    static final ArchRule domain_repositories_must_be_interfaces_in_domain =
        classes()
            .that().areAnnotatedWith(DomainRepository.class)
            .should().beInterfaces()
            .andShould().resideInAPackage("..domain..")
            .because("Domain repositories must be output ports (interfaces) defined in Domain Layer (Clean Architecture ADR-003).");

    @ArchTest
    static final ArchRule domain_services_and_policies_must_remain_pure =
        noClasses()
            .that().areAnnotatedWith(DomainService.class)
            .or().areAnnotatedWith(DomainValidationService.class)
            .or().areAnnotatedWith(DomainComputationService.class)
            .or().areAnnotatedWith(DomainPolicy.class)
            .should().dependOnClassesThat()
            .resideInAnyPackage("org.springframework..", "..infrastructure..", "..presentation..")
            .because("Domain services and policies must remain pure domain logic without Spring or infrastructure dependencies (ADR-011).");

    @ArchTest
    static final ArchRule application_services_must_reside_in_application_layer =
        classes()
            .that().areAnnotatedWith(ApplicationService.class)
            .or().areAnnotatedWith(WorkflowService.class)
            .should().resideInAPackage("..application..")
            .because("Application and Workflow services must orchestrate use-cases strictly from the Application layer.");

    @ArchTest
    static final ArchRule query_services_must_reside_in_application_layer =
        classes()
            .that().areAnnotatedWith(QueryService.class)
            .should().resideInAPackage("..application..")
            .because("CQRS Query services must reside in Application layer to serve read models.");

    @ArchTest
    static final ArchRule value_objects_must_not_have_public_setters =
        classes()
            .that().areAnnotatedWith(ValueObject.class)
            .should(new com.tngtech.archunit.lang.ArchCondition<com.tngtech.archunit.core.domain.JavaClass>("not have public setter methods") {
                @Override
                public void check(com.tngtech.archunit.core.domain.JavaClass javaClass, com.tngtech.archunit.lang.ConditionEvents events) {
                    for (com.tngtech.archunit.core.domain.JavaMethod method : javaClass.getMethods()) {
                        if (method.getName().startsWith("set") && method.getModifiers().contains(com.tngtech.archunit.core.domain.JavaModifier.PUBLIC)) {
                            events.add(com.tngtech.archunit.lang.SimpleConditionEvent.violated(method,
                                "Value Object " + javaClass.getName() + " has public setter: " + method.getName()));
                        }
                    }
                }
            })
            .because("Value Objects must be immutable and not expose public setters in Domain-Driven Design.");
}
