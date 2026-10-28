package ro.uaic.feaa.sdbis.supplychain.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * ADR-001: Dependency Cycles Verification.
 * Verifica faptul ca nucleele de domeniu (Domain Layers) sunt strict aciclice.
 */
@AnalyzeClasses(
    packages = "ro.uaic.feaa.sdbis.supplychain",
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class DependencyCycleTest {

    @ArchTest
    static final ArchRule domain_slices_must_be_free_of_cycles =
        slices()
            .matching("ro.uaic.feaa.sdbis.supplychain.(*).domain..")
            .should().beFreeOfCycles()
            .because("Domain layers of bounded contexts must be strictly acyclic (ADR-001).");
}
