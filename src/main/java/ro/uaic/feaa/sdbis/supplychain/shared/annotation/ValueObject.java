package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip DDD pentru un obiect valoare (Value Object).
 * Un obiect imutabil definit exclusiv prin atributele sale structurale,
 * fara o identitate separata (ID).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValueObject {
    String value() default "";
}
