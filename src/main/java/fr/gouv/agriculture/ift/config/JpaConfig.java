package fr.gouv.agriculture.ift.config;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.gouv.agriculture.ift.IftApiApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.SQLException;

// @EntityScan used to handle LocalDate and LocalDateTime (JSR-310) with JPA
@EntityScan(basePackageClasses = {IftApiApplication.class, Jsr310JpaConverters.class})
// Enable @CreateDate, @CreateBy, @LastModifiedDate and @LastModifiedBy annotations
@EnableJpaAuditing
@Configuration
public class JpaConfig {

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.hikari.connectionTimeout:30000}")
    private String connectionTimeout;

    @Value("${spring.datasource.hikari.maximumPoolSize:100}")
    private String maximumPoolSize;

    @Value("${spring.datasource.hikari.maxLifetime:1800000}")
    private String maxLifetime;

    @Bean
    public DataSource dataSource() throws SQLException, ClassNotFoundException {

        // Mandatory to use HikariCP within a non-embedded Tomcat
        if (!StringUtils.isEmpty(driverClassName)) {
            Class.forName(driverClassName);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setConnectionTimeout(Long.parseLong(connectionTimeout));
        config.setMaxLifetime(Long.parseLong(maxLifetime));
        config.setMaximumPoolSize(Integer.parseInt(maximumPoolSize));

        return new HikariDataSource(config);
    }
}