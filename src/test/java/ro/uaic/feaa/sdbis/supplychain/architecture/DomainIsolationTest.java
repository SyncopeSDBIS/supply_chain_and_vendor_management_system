package ro.uaic.feaa.sdbis.supplychain.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * ADR-011: Pure Domain Isolation Test.
 * Verifica faptul ca pachetele domain nu depind de Spring, Hibernate, Jackson,
 * JAXB, JMS sau Straturile exterioare.
 */
@AnalyzeClasses(packages = "ro.uaic.feaa.sdbis.supplychain", importOptions = ImportOption.DoNotIncludeTests.class)
public class DomainIsolationTest {

    @ArchTest
    static final ArchRule domain_must_not_depend_on_spring_or_infrastructure_frameworks = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                    "org.springframework..",
                    "org.hibernate..",
                    "com.fasterxml.jackson..",
                    "jakarta.xml.bind..",
                    "jakarta.jms..",
                    "com.stripe..",
                    "..infrastructure..",
                    "..presentation..",
                    "..application..")
            .because(
                    "Domain Layer must remain agnostic of Spring, Hibernate internals, REST/JSON/XML, JMS, Stripe, and outer layers (ADR-011).");
}
