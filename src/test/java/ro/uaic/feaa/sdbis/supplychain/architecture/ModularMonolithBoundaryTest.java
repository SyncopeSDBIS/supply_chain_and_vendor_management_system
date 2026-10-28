package ro.uaic.feaa.sdbis.supplychain.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * ArchUnit Verification for Strict Modular Monolith Boundaries (ADR-002, Master Blueprint).
 *
 * Enforces that internal module packages (application, domain aggregates/services, infrastructure)
 * are completely encapsulated and can NEVER be accessed by external bounded contexts.
 * External contexts may only interact via public 'api' packages or published domain events.
 */
@AnalyzeClasses(
    packages = "ro.uaic.feaa.sdbis.supplychain",
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class ModularMonolithBoundaryTest {

    @ArchTest
    static final ArchRule external_modules_cannot_access_comenzi_internals =
        noClasses()
            .that().resideInAnyPackage("..calitate..", "..facturi..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "..comenzi.domain",
                "..comenzi.application",
                "..comenzi.application.command..",
                "..comenzi.application.query..",
                "..comenzi.infrastructure.."
            )
            .because("External modules may only interact with Comenzi via comenzi.api or comenzi.domain.event");

    @ArchTest
    static final ArchRule external_modules_cannot_access_calitate_internals =
        noClasses()
            .that().resideInAnyPackage("..comenzi..", "..facturi..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "..calitate.domain",
                "..calitate.domain.policy..",
                "..calitate.domain.validation..",
                "..calitate.application",
                "..calitate.application.command..",
                "..calitate.application.query..",
                "..calitate.infrastructure.."
            )
            .because("External modules may only interact with Calitate via calitate.api or calitate.domain.event");

    @ArchTest
    static final ArchRule external_modules_cannot_access_facturi_internals =
        noClasses()
            .that().resideInAnyPackage("..comenzi..", "..calitate..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "..facturi.domain",
                "..facturi.domain.policy..",
                "..facturi.domain.validation..",
                "..facturi.application",
                "..facturi.application.command..",
                "..facturi.application.query..",
                "..facturi.infrastructure.."
            )
            .because("External modules may only interact with Facturi via facturi.api or facturi.domain.event");
}
