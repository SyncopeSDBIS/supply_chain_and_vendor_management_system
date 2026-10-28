package ro.uaic.feaa.sdbis.supplychain;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Servlet Web Initializer enabling deployment on traditional Apache Tomcat servlet containers.
 * Required by standard enterprise enterprise deployment specifications (Project_OOAD).
 */
public class WebInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SupplyChainApplication.class);
    }
}
