package dev.oauth.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "dev.oauth.user.repository",
        entityManagerFactoryRef = "authEntityManagerFactory",
        transactionManagerRef = "authTransactionManager"
)
public class AuthDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties authDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource authDataSource() {
        return authDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = {"authEntityManagerFactory", "entityManagerFactory"})
    @Primary
    public LocalContainerEntityManagerFactoryBean authEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            JpaProperties jpaProperties
    ) {
        return builder
                .dataSource(authDataSource())
                .packages("dev.oauth.user.entity")
                .persistenceUnit("auth")
                .properties(jpaVendorProperties(jpaProperties))
                .build();
    }

    @Bean(name = {"authTransactionManager", "transactionManager"})
    @Primary
    public PlatformTransactionManager authTransactionManager(
            @Qualifier("authEntityManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    private Map<String, Object> jpaVendorProperties(JpaProperties jpaProperties) {
        return new HashMap<>(jpaProperties.getProperties());
    }
}
