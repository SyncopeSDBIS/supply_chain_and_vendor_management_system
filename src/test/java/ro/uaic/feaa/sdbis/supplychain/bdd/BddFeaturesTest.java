package ro.uaic.feaa.sdbis.supplychain.bdd;

import io.cucumber.core.cli.Main;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("BDD Feature Execution Test (All 6 Gherkin features)")
public class BddFeaturesTest {

    @Test
    @DisplayName("Executa toate cele 6 fisiere Gherkin (.feature) in limba engleza")
    void runAllCucumberFeatures() {
        File dir = new File("src/test/resources/features");
        File[] files = dir.listFiles((d, name) -> name.endsWith(".feature"));


        List<String> args = new ArrayList<>();
        args.add("--glue");
        args.add("ro.uaic.feaa.sdbis.supplychain.bdd");
        args.add("--plugin");
        args.add("pretty");

        if (files != null) {
            for (File f : files) {
                args.add(f.getPath().replace('\\', '/'));
            }
        }

        byte exitCode = Main.run(args.toArray(new String[0]), Thread.currentThread().getContextClassLoader());
        assertEquals(0, exitCode, "Toate scenariile Cucumber BDD trebuie sa treaca cu succes!");
    }
}
