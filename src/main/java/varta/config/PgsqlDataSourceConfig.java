package varta.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import   org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
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

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "varta.repository.pgsql",
        entityManagerFactoryRef = "pgsqlEntityManagerFactory",
        transactionManagerRef = "pgsqlTransactionManager"
)
public class PgsqlDataSourceConfig {

    @Primary
    @Bean(name = "pgsqlProperties")
    @ConfigurationProperties(prefix = "spring.datasource.pgsql")
    public DataSourceProperties pgsqlProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "dataSource")
    public DataSource dataSource(@Qualifier("pgsqlDataSource") DataSource pgsqlDataSource) {
        return pgsqlDataSource;
    }

    @Primary
    @BatchDataSource
    @Bean(name = "pgsqlDataSource")
    public DataSource pgsqlDataSource(@Qualifier("pgsqlProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean(name = "pgsqlEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean pgsqlEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("pgsqlDataSource") DataSource dataSource) {

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        return builder
                .dataSource(dataSource)
                .packages("varta.model.pgsql")
                .persistenceUnit("pgsql")
                .properties(properties)
                .build();
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("pgsqlTransactionManager") PlatformTransactionManager transactionManager) {
        return transactionManager;
    }

    @Primary
    @Bean(name = "pgsqlTransactionManager")
    public PlatformTransactionManager pgsqlTransactionManager(
            @Qualifier("pgsqlEntityManagerFactory") LocalContainerEntityManagerFactoryBean pgsqlEntityManagerFactory) {
        return new JpaTransactionManager(pgsqlEntityManagerFactory.getObject());
    }
}