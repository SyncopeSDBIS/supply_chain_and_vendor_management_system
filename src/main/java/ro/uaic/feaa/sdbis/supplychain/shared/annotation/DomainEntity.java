package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip DDD pentru o entitate de domeniu (Entity).
 * Un obiect care are identitate de sine statatoare (ID) si ciclu de viata,
 * dar este controlat din cadrul unui Aggregate Root.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DomainEntity {
    String value() default "";
}
