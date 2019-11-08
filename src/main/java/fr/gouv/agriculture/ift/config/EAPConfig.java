package fr.gouv.agriculture.ift.config;

import fr.gouv.agriculture.ift.service.ConfigurationService;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static fr.gouv.agriculture.ift.Constants.CONF_AUTH_EAP_ENDPOINT_URL;

@Configuration
public class EAPConfig {

    private String url;

    @Autowired
    public EAPConfig(ConfigurationService configurationService) {
        url = configurationService.getValue(CONF_AUTH_EAP_ENDPOINT_URL);
    }

    @Bean
    public TicketValidator getTicketValidator() {
        return new Cas20ServiceTicketValidator(url);
    }

    public String getServiceURL() {
        return url;
    }

}
