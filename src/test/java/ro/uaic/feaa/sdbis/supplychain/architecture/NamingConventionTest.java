package ro.uaic.feaa.sdbis.supplychain.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Verificarea Conventiilor de Denumire conform cerintelor de arhitectura OOAD:
 * - Servicii: *Service
 * - Controllere REST: *Ctrl
 * - Comenzi: *Command
 * - DTO-uri: *DTO sau *Request
 */
@AnalyzeClasses(
    packages = "ro.uaic.feaa.sdbis.supplychain",
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class NamingConventionTest {

    @ArchTest
    static final ArchRule services_should_be_named_service =
        classes()
            .that().areAnnotatedWith(Service.class)
            .should().haveSimpleNameEndingWith("Service")
            .because("Spring service classes must follow *Service naming convention.");

    @ArchTest
    static final ArchRule controllers_should_be_named_ctrl =
        classes()
            .that().resideInAPackage("..presentation..")
            .and().areAnnotatedWith(RestController.class)
            .should().haveSimpleNameEndingWith("Ctrl")
            .because("REST presentation controllers must follow *Ctrl naming convention.");

    @ArchTest
    static final ArchRule commands_should_be_named_command =
        classes()
            .that().resideInAPackage("..application.command..")
            .should().haveSimpleNameEndingWith("Command")
            .because("Application command records must follow *Command naming convention.");

    @ArchTest
    static final ArchRule dtos_should_be_named_dto_or_request =
        classes()
            .that().resideInAPackage("..application.dto..")
            .should().haveSimpleNameEndingWith("DTO")
            .orShould().haveSimpleNameEndingWith("Request")
            .because("Application DTOs must follow *DTO or *Request naming convention.");
}
