package ro.uaic.feaa.sdbis.supplychain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot Application for Supply Chain and Vendor Management System (P2P).
 * Aligned 1:1 with Project_OOAD.docx and Sprints 1-4.
 */
@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = "ro.uaic.feaa.sdbis.supplychain")
public class SupplyChainApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplyChainApplication.class, args);
    }
}
