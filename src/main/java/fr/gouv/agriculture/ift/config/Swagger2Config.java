package fr.gouv.agriculture.ift.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import static fr.gouv.agriculture.ift.Constants.API_ROOT;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
public class Swagger2Config {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("ift")
                .directModelSubstitute(LocalDateTime.class, String.class)
                .directModelSubstitute(LocalDate.class, String.class)
                .directModelSubstitute(LocalTime.class, String.class)
                .directModelSubstitute(ZonedDateTime.class, String.class).apiInfo(apiInfo())
                .ignoredParameterTypes(Pageable.class)
                .useDefaultResponseMessages(false)
                .select()
                .paths(regex(API_ROOT + "/.*"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("API REST pour IFT")
                .contact(new Contact("agrilab", "https://github.com/AGRILAB", "agrilab@agriculture.gouv.fr"))
                .license("Licence ouverte")
                .licenseUrl("https://www.etalab.gouv.fr/licence-ouverte-open-licence")
                .version("1.0")
                .build();
    }
}