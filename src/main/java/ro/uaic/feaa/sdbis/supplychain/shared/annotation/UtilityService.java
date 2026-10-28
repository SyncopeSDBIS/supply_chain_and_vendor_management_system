package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip SOA pentru servicii utilitare transversale (Utility Service / Cross-Cutting Service).
 * Marchează componentele responsabile de funcționalități transversale reutilizabile în cadrul soluției:
 * audit operațional imutabil (JurnalAuditService), servicii de notificare (NotificariDispatcherService) etc.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UtilityService {
    String value() default "";
}
