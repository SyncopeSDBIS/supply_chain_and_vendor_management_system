package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip DDD pentru un serviciu din Application Layer (Application Service / Use-Case Service).
 * Responsabil de coordonarea fluxurilor de utilizare (Use Cases), managementul tranzacționalității,
 * orchestrarea agregatelor, colaborarea cu repository-urile de domeniu și publicarea
 * evenimentelor de domeniu către Transactional Outbox.
 * Nu conține reguli de business (care aparțin agregatelor și serviciilor de domeniu).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApplicationService {
    String value() default "";
}
