package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip semantic pentru servicii de validare (Validation Service conform cerințelor din Sprint 2 / SOA).
 * Marchează contractele și implementările serviciilor de validare a regulilor de business.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidationService {
    String value() default "";
}
