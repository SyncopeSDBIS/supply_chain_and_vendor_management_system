package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip DDD / SOA pentru servicii de validare a logicii de domeniu (Domain Validation Service).
 * Marchează serviciile responsabile de verificarea invarianților complecși de business,
 * a integrității cross-entity și cross-aggregate (ex: 3-Way Matching, verificarea eligibilității la plată).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DomainValidationService {
    String value() default "";
}
