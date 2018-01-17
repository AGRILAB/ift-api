package fr.gouv.agriculture.ift.config;

import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EAPConfig {

    @Value("${auth.eap.endpoint.url}")
    private String url;

    @Bean
    public TicketValidator getTicketValidator() {
        return new Cas20ServiceTicketValidator(url);
    }

    public String getServiceURL() {
        return url;
    }

}
