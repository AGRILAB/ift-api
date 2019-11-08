package fr.gouv.agriculture.ift.config;

import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.AbstractPathProvider;
import springfox.documentation.spring.web.paths.Paths;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.ServletContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import static fr.gouv.agriculture.ift.Constants.API_ADMIN_ROOT;
import static fr.gouv.agriculture.ift.Constants.API_WELL_KNOWN;
import static fr.gouv.agriculture.ift.Constants.AUTH;

@Configuration
public class Swagger2Config {

    @Value("${swagger.path:/}")
    private String basePath;

    private final ServletContext servletContext;

    public Swagger2Config(ServletContext servletContext){
        this.servletContext = servletContext;
    }
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("ift")
                .directModelSubstitute(LocalDateTime.class, String.class)
                .directModelSubstitute(LocalDate.class, String.class)
                .directModelSubstitute(LocalTime.class, String.class)
                .directModelSubstitute(ZonedDateTime.class, String.class).apiInfo(apiInfo())
                .pathProvider(new RelativePathProvider(servletContext){
                    @Override
                    public String getApplicationBasePath(){
                        return basePath;
                    }
                })
                .ignoredParameterTypes(Pageable.class)
                .useDefaultResponseMessages(false)
                .select()
                .paths(Predicates.not(PathSelectors.regex(API_ADMIN_ROOT + "/.*")))
                .paths(Predicates.not(PathSelectors.regex(AUTH + "/.*")))
                .paths(Predicates.not(PathSelectors.regex( API_WELL_KNOWN + "/.*")))
                .paths(Predicates.not(PathSelectors.regex("/error")))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("API REST pour IFT")
                .contact(new Contact("Initial", "https://github.com/minagri-initial", "calculette-ift.dgpaat@agriculture.gouv.fr"))
                .license("Licence ouverte")
                .licenseUrl("https://www.etalab.gouv.fr/licence-ouverte-open-licence")
                .version("1.0")
                .description("API REST pour IFT")
                .build();
    }

    protected String getHost() {
        String host = null;
        try {
            host = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return host;
    }

    protected class BasePathAwareRelativePathProvider extends AbstractPathProvider {
        private String basePath;

        public BasePathAwareRelativePathProvider(String basePath) {
            this.basePath = basePath;
        }

        @Override
        protected String applicationPath() {
            return basePath;
        }

        @Override
        protected String getDocumentationPath() {
            return "/";
        }

        @Override
        public String getOperationPath(String operationPath) {
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath("/");
            return Paths.removeAdjacentForwardSlashes(
                    uriComponentsBuilder.path(operationPath.replaceFirst(basePath, "")).build().toString());
        }
    }
}