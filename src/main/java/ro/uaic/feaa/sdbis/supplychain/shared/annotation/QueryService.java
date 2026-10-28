package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip CQRS pentru servicii dedicate interogărilor și proiecțiilor (Query Service / Entity View Computation).
 * Responsabil de extragerea și asamblarea modelelor de citire optimizate (Read Models, Summary Views, Dashboard Views),
 * fără a modifica starea agregatelor (operațiuni read-only, idempotente).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryService {
    String value() default "";
}
