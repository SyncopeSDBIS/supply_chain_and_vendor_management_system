package ro.uaic.feaa.sdbis.supplychain.facturi.application.port;

/**
 * Port de configurare pentru integrarea Stripe in Clean Architecture (FIX ARCH-01).
 * Implementat de StripeProperties in stratul de infrastructura.
 */
public interface IStripeConfigPort {
    String getWebhookSecret();
}
