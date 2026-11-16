package ro.uaic.feaa.sdbis.supplychain.facturi.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ro.uaic.feaa.sdbis.supplychain.facturi.application.port.IStripeConfigPort;

@Component
@ConfigurationProperties(prefix = "stripe")
public class StripeProperties implements IStripeConfigPort {

    private String apiKey = "sk_test_mock_sandbox_key_12345";
    private String webhookSecret = "whsec_mock_sandbox_secret_12345";
    private String currency = "eur";
    private boolean simulationMode = true;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }

    public void setWebhookSecret(String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isSimulationMode() {
        return simulationMode;
    }

    public void setSimulationMode(boolean simulationMode) {
        this.simulationMode = simulationMode;
    }
}
