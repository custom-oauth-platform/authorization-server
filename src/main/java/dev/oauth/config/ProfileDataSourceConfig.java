package dev.oauth.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "dev.oauth.profile.repository",
        entityManagerFactoryRef = "profileEntityManagerFactory",
        transactionManagerRef = "profileTransactionManager"
)
public class ProfileDataSourceConfig {

    @Bean
    @ConfigurationProperties("app.datasource.profile")
    public DataSourceProperties profileDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource profileDataSource() {
        return profileDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "profileEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean profileEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            JpaProperties jpaProperties
    ) {
        return builder
                .dataSource(profileDataSource())
                .packages("dev.oauth.profile.entity")
                .persistenceUnit("profile")
                .properties(jpaVendorProperties(jpaProperties))
                .build();
    }

    @Bean(name = "profileTransactionManager")
    public PlatformTransactionManager profileTransactionManager(
            @Qualifier("profileEntityManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    private Map<String, Object> jpaVendorProperties(JpaProperties jpaProperties) {
        Map<String, Object> properties = new HashMap<>(jpaProperties.getProperties());
        properties.putIfAbsent("hibernate.hbm2ddl.auto", "none");
        return properties;
    }
}
