package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip DDD / SOA pentru o fabrică de domeniu (Domain Factory / Entity Service).
 * Responsabil de instanțierea și asamblarea agregatelor complexe sau a entităților
 * asigurând că obiectul creat se află încă de la început într-o stare validă și conformă invarianților.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DomainFactory {
    String value() default "";
}
