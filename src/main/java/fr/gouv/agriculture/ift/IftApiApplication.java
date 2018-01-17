package fr.gouv.agriculture.ift;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.PropertySource;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@PropertySource(value = "classpath:env.properties", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:env-${spring.profiles.active}.properties", ignoreResourceNotFound = true)
@EnableSwagger2
@EnableCaching
public class IftApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(IftApiApplication.class, args);
    }
}

