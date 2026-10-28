package ro.uaic.feaa.sdbis.supplychain.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import ro.uaic.feaa.sdbis.supplychain.comenzi.domain.Lot;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.OrdinPlata;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.constructors;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

/**
 * ADR-002: Aggregate Encapsulation Verification.
 * Garanteaza ca entitatile copil (ex: Lot) si entitatile create strict prin Aggregate Root (ex: OrdinPlata)
 * nu pot fi mutate sau instantiate din afara agregatului lor.
 */
@AnalyzeClasses(
    packages = "ro.uaic.feaa.sdbis.supplychain",
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class AggregateEncapsulationTest {

    @ArchTest
    static final ArchRule lot_mutation_methods_must_be_package_private =
        methods()
            .that().areDeclaredIn(Lot.class)
            .and().haveNameMatching("expediaza|receptioneaza|plaseazaInCarantina|elibereazaInStoc|respinge|returneazaFurnizorului|anuleaza")
            .should().bePackagePrivate()
            .because("Lot mutation methods must be package-private to enforce ComandaAprovizionare aggregate boundary (Tabelul 2.1).");

    @ArchTest
    static final ArchRule ordin_plata_business_constructor_must_be_package_private =
        constructors()
            .that().areDeclaredIn(OrdinPlata.class)
            .and().haveRawParameterTypes(java.util.UUID.class, ro.uaic.feaa.sdbis.supplychain.facturi.domain.Suma.class, java.util.UUID.class)
            .should().bePackagePrivate()
            .because("OrdinPlata must only be instantiated by FacturaFurnizor.creeazaOrdinPlata() (Control Financiar ADR-002).");
}
