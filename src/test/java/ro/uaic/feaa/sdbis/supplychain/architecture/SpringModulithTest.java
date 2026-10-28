package ro.uaic.feaa.sdbis.supplychain.architecture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import ro.uaic.feaa.sdbis.supplychain.SupplyChainApplication;

@DisplayName("Spring Modulith Structural Verification (ADR-002)")
public class SpringModulithTest {

    @Test
    @DisplayName("Verifica ca structura monolitului modular respecta regulile Spring Modulith")
    void verifyModularMonolith() {
        ApplicationModules modules = ApplicationModules.of(SupplyChainApplication.class);
        System.out.println("DEBUG SPRING MODULITH DETECTED MODULES: " + modules);
        modules.verify();
    }
}
