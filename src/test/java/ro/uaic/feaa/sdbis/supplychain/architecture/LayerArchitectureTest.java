package ro.uaic.feaa.sdbis.supplychain.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * ADR-003: Clean / Layered Architecture Verification.
 * Garanteaza fluxul corect al dependentelor intre layere conform Figurilor 7-10 din Project_OOAD.docx:
 * - Domain nu depinde de Application, Presentation sau Infrastructure.
 * - Application nu depinde de Presentation sau Infrastructure.
 */
@AnalyzeClasses(
    packages = "ro.uaic.feaa.sdbis.supplychain",
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class LayerArchitectureTest {

    @ArchTest
    static final ArchRule domain_layer_must_not_depend_on_outer_layers =
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..presentation..", "..application..", "..infrastructure..")
            .because("Domain layer must be agnostic of outer layers (Clean Architecture ADR-003).");

    @ArchTest
    static final ArchRule application_layer_must_not_depend_on_presentation =
        noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..presentation..")
            .because("Application layer orchestrates domain and must not depend on Presentation/REST.");
}
