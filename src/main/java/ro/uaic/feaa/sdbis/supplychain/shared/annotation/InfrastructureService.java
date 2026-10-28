package ro.uaic.feaa.sdbis.supplychain.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotip Clean Architecture pentru servicii din Infrastructure Layer (Infrastructure Service).
 * Marchează componentele tehnice, adaptoarele către sisteme externe (REST, Stripe),
 * procesele de fundal (Schedulers) și mecanismele de mesagerie asincronă (Outbox/Inbox Dispatchers).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InfrastructureService {
    String value() default "";
}
