package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip semantic pentru servicii de calcul (Computation Service conform cerințelor din Sprint 2 / SOA).
 * Marchează contractele și implementările serviciilor de calcul numeric și indicatori de business.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ComputationService {
    String value() default "";
}
