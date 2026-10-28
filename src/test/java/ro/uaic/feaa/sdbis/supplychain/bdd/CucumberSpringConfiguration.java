package ro.uaic.feaa.sdbis.supplychain.bdd;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@ContextConfiguration(classes = CucumberSpringConfiguration.EmptyConfig.class)
public class CucumberSpringConfiguration {

    @Configuration
    public static class EmptyConfig {
    }
}
