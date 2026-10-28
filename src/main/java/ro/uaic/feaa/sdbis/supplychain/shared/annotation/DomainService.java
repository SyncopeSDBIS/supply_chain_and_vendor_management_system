package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip DDD pentru un serviciu de domeniu (Domain Service / Task Service).
 * Marchează componentele din Domain Layer care încapsulează logică de business pură,
 * reguli operaționale sau algoritmi ce implică multiple agregate și nu aparțin
 * în mod natural unei singure entități sau rădăcini de agregat.
 * Rămâne complet decuplat de Spring sau alte framework-uri de infrastructură (ADR-011).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DomainService {
    String value() default "";
}
