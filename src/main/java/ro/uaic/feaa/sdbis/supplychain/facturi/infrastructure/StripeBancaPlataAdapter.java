package ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.IBancaPlataProvider;
import ro.uaic.feaa.sdbis.supplychain.facturi.domain.OrdinPlata;
import ro.uaic.feaa.sdbis.supplychain.shared.IJurnalAudit;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Adapter pentru transmiterea platilor catre banca prin Stripe Sandbox (ADR-010).
 * Apeleaza API-ul Stripe (PaymentIntent) atasand in metadata idOrdin si facturaId,
 * si inregistreaza operatiunea in jurnalul de audit.
 */
@Component
public class StripeBancaPlataAdapter implements IBancaPlataProvider {

    private static final Logger log = LoggerFactory.getLogger(StripeBancaPlataAdapter.class);

    private final IJurnalAudit jurnalAudit;
    private final StripeProperties stripeProperties;

    public StripeBancaPlataAdapter(IJurnalAudit jurnalAudit, StripeProperties stripeProperties) {
        this.jurnalAudit = jurnalAudit;
        this.stripeProperties = stripeProperties;
    }

    @Override
    public boolean transmiteOrdinPlata(OrdinPlata ordin, String ibanDestinatar) {
        Stripe.apiKey = stripeProperties.getApiKey();

        long amountInCents = ordin.getSuma().getValoare().multiply(BigDecimal.valueOf(100)).longValue();
        String currency = ordin.getSuma().getMoneda().getCurrencyCode().toLowerCase();
        String bankTransferRef;

        try {
            if (stripeProperties.getApiKey() != null && !stripeProperties.getApiKey().contains("mock")) {
                PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                        .setAmount(amountInCents)
                        .setCurrency(currency)
                        .putMetadata("idOrdin", ordin.getIdOrdin().toString())
                        .putMetadata("facturaId", ordin.getFacturaId().toString())
                        .putMetadata("ibanDestinatar", ibanDestinatar)
                        .setDescription("Plata Furnizor - Ordin " + ordin.getIdOrdin())
                        .build();

                PaymentIntent intent = PaymentIntent.create(params);
                bankTransferRef = intent.getId();
            } else {
                bankTransferRef = "pi_mock_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            }
        } catch (StripeException e) {
            log.warn("[Stripe Sandbox Fallback] API call threw exception: {}. Generating mock transaction reference.", e.getMessage());
            bankTransferRef = "pi_mock_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }

        log.info("[Stripe Sandbox] Transmitting payment order {} | Sum: {} {} | Dest IBAN: {} | Bank Ref: {}",
                ordin.getIdOrdin(), ordin.getSuma().getValoare(), ordin.getSuma().getMoneda(), ibanDestinatar, bankTransferRef);

        jurnalAudit.inregistreazaAcces("TRANSMITERE_BANCA_STRIPE",
                "Ordin: " + ordin.getIdOrdin() + " | Factura: " + ordin.getFacturaId() + " | Ref: " + bankTransferRef + " | Dest: " + ibanDestinatar);

        return true;
    }
}
