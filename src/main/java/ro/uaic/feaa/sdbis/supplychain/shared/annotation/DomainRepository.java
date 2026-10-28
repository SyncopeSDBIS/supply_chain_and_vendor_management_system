package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip DDD pentru o interfata de Repository de domeniu (Output Port).
 * Ofera acces de tip colectie la agregatele din domeniu, independent de infrastructura tehnica.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DomainRepository {
    String value() default "";
}
