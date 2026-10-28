package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip DDD / SOA pentru servicii de calcul în domeniu (Domain Computation Service).
 * Marchează serviciile responsabile de calculul formulelor de business, indicatorilor statistici,
 * scorurilor agregate și derivarea atributelor complexe (ex: Scorecard lunar, estimare lead time, reconciliere).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DomainComputationService {
    String value() default "";
}
