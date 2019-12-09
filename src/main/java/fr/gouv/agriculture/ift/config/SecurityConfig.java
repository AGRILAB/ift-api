package fr.gouv.agriculture.ift.config;

import fr.gouv.agriculture.ift.security.JWTAuthenticatedFilter;
import fr.gouv.agriculture.ift.security.TokenAuthenticationService;
import fr.gouv.agriculture.ift.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collections;

import static fr.gouv.agriculture.ift.Constants.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private TokenAuthenticationService tokenAuthenticationService;

    @Autowired
    public SecurityConfig(ConfigurationService configurationService) {
        long expirationDelay = configurationService.getValueAsLong(CONF_JWT_TOKEN_EXPIRATION) * 60 * 1000;
        String secret = configurationService.getValue(CONF_JWT_TOKEN_SECRET);

        tokenAuthenticationService = new TokenAuthenticationService(secret, expirationDelay);

    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }

    /**
     * Http Security Configuration
     */
    @Override
    protected void configure(final HttpSecurity http) throws Exception {

        // Allow X-Frame-Options for same origin (useful for H2-Console)
        http.headers().frameOptions().sameOrigin();

        // Disable CSRF for JWT usage
        http.csrf().disable();
        // Apply the Authentication and Authorization Strategies your application endpoints require
        authorizeRequests(http);
        // STATELESS - we want re-authentication of JWT token on every request
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    /**
     * Lightweight default configuration that offers basic authorization checks for authenticated
     * users on secured endpoint, and sets up a Principal user object with granted authorities
     */
    protected void authorizeRequests(final HttpSecurity http) throws Exception {
        http.cors().and()
                .authorizeRequests()

                // Swagger endpoint
                .antMatchers("/swagger-ui.html",
                        "/v2/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/springfox-swagger-ui/**",
                        "/configuration/ui/**",
                        "/configuration/security/**").permitAll()

                // Protected Actuator endpoints
                .antMatchers(API_ADMIN_ROOT + "/**").hasAuthority(ROLE_ADMIN)

                // Public API
                .antMatchers(API_HELLO_ROOT + "/**",
                        API_CAMPAGNES_ROOT + "/**",
                        API_GROUPES_CULTURES_ROOT + "/**",
                        API_SEGMENTS_ROOT + "/**",
                        API_TYPES_TRAITEMENTS_ROOT + "/**",
                        API_CULTURES_ROOT + "/**",
                        API_CIBLES_ROOT + "/**",
                        API_UNITES_ROOT,
                        API_NUMEROS_AMM_ROOT + "/**",
                        API_PRODUITS_ROOT + "/**",
                        API_DOSES_REFERENCE_ROOT + "/**",
                        API_PRODUITS_DOSES_REFERENCE_ROOT + "/**",
                        API_AVERTISSEMENTS_ROOT + "/**",
                        API_AVIS_ROOT + "/**",
                        API_IFT_ROOT + "/**").permitAll()

                // Authentication API
                .antMatchers(AUTH + "/**").permitAll()

                // By default permitAll, to return 404 if the route does not exists
                .anyRequest().permitAll()
                .and()

                // And filter other requests to check the presence of JWT in header but the provided excluded requestURIs
                .addFilterBefore(new JWTAuthenticatedFilter(tokenAuthenticationService, Collections.singletonList(API_ADMIN_ROOT + "/**")),
                        UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * Enable CORS
     * https://spring.io/blog/2015/06/08/cors-support-in-spring-framework
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public SecureRandom getSecureRandom() {
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            //log.info("SecureRandom initialized.");
            return random;
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Bean
    public TokenAuthenticationService getAuthenticationService() {
        return tokenAuthenticationService;
    }
}