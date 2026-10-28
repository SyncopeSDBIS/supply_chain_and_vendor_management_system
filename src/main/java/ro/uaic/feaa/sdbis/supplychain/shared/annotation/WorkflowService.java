package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip SOA / Application pentru servicii de workflow operațional (Workflow Service / Orchestration Service).
 * Marchează contractele și implementările fluxurilor operaționale complexe ale aplicației
 * (ex: flux aprovizionare comenzi, workflow recepție și calificare furnizori, workflow reconciliere și plăți).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WorkflowService {
    String value() default "";
}
