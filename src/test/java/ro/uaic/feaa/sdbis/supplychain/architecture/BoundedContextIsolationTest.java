package ro.uaic.feaa.sdbis.supplychain.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Verifica izolarea stricta a celor 3 Bounded Context-uri (comenzi, calitate, facturi).
 * Ele comunica prin shared kernel, REST Adapters sau Evenimente.
 */
@AnalyzeClasses(
    packages = "ro.uaic.feaa.sdbis.supplychain",
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class BoundedContextIsolationTest {

    @ArchTest
    static final ArchRule comenzi_domain_must_not_depend_on_calitate_or_facturi =
        noClasses()
            .that().resideInAPackage("..comenzi.domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..calitate..", "..facturi..")
            .because("Comenzi domain must be isolated from other bounded contexts.");

    @ArchTest
    static final ArchRule calitate_domain_must_not_depend_on_comenzi_or_facturi =
        noClasses()
            .that().resideInAPackage("..calitate.domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..comenzi..", "..facturi..")
            .because("Calitate domain must be isolated from other bounded contexts.");

    @ArchTest
    static final ArchRule facturi_domain_must_not_depend_on_comenzi_or_calitate =
        noClasses()
            .that().resideInAPackage("..facturi.domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..comenzi..", "..calitate..")
            .because("Facturi domain must be isolated from other bounded contexts.");
}
