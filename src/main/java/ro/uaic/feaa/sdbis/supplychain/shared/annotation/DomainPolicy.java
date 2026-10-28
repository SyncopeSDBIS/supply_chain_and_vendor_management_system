package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip DDD pentru o politică de business (Domain Policy / Strategy).
 * Marchează clasele ce implementează reguli decizionale configurabile sau algoritmi de decizie
 * de domeniu conform Strategy Pattern (ex: Politica de Aprobare Comenzi, Politica de Autorizare a Plăților,
 * Politica de Selecție Plan AQL conform ISO 2859-1).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DomainPolicy {
    String value() default "";
}
