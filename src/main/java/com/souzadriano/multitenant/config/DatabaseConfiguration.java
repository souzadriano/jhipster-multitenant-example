package com.souzadriano.multitenant.config;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.h2.tools.Server;
import org.hibernate.MultiTenancyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.codahale.metrics.MetricRegistry;
import com.souzadriano.multitenant.config.multitenant.CurrentTenantIdentifierResolverImpl;
import com.souzadriano.multitenant.config.multitenant.MultiTenantConnectionProviderImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.github.jhipster.config.JHipsterConstants;
import io.github.jhipster.config.liquibase.AsyncSpringLiquibase;
import liquibase.integration.spring.SpringLiquibase;

@Configuration
@EnableJpaRepositories("com.souzadriano.multitenant.repository")
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class DatabaseConfiguration {
	
	@Autowired MultiTenantConnectionProviderImpl dsProvider;

    @Autowired CurrentTenantIdentifierResolverImpl tenantResolver;

    private final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);

    private final Environment env;
    
    @Autowired ApplicationContext context;
    
    @Autowired(required = false)
    private MetricRegistry metricRegistry;

    public DatabaseConfiguration(Environment env) {
        this.env = env;
    }

    /**
     * Open the TCP port for the H2 database, so it is available remotely.
     *
     * @return the H2 database TCP server
     * @throws SQLException if the server failed to start
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)
    public Server h2TCPServer() throws SQLException {
        return Server.createTcpServer("-tcp","-tcpAllowOthers");
    }

    @Bean
    public SpringLiquibase liquibase(@Qualifier("taskExecutor") TaskExecutor taskExecutor,
            DataSource dataSource, LiquibaseProperties liquibaseProperties) {

        // Use liquibase.integration.spring.SpringLiquibase if you don't want Liquibase to start asynchronously
        SpringLiquibase liquibase = new AsyncSpringLiquibase(taskExecutor, env);
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:config/liquibase/master.xml");
        liquibase.setContexts(liquibaseProperties.getContexts());
        liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
        if (env.acceptsProfiles(JHipsterConstants.SPRING_PROFILE_NO_LIQUIBASE)) {
        	liquibase.setShouldRun(false);
        } else {
            liquibase.setShouldRun(liquibaseProperties.isEnabled());
            log.debug("Configuring Liquibase");
        }
        return liquibase;
    }
    
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        // Generate DDL is not supported in Hibernate to multi-tenancy features
        // https://hibernate.atlassian.net/browse/HHH-7395
        hibernateJpaVendorAdapter.setGenerateDdl(false);
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(env, "spring.jpa.");
        hibernateJpaVendorAdapter.setDatabase(Database.valueOf(propertyResolver.getProperty("database")));
        hibernateJpaVendorAdapter.setShowSql(Boolean.valueOf(propertyResolver.getProperty("show-sql")));
        hibernateJpaVendorAdapter.setDatabasePlatform(propertyResolver.getProperty("database-platform"));
        return hibernateJpaVendorAdapter;
    }

    
    @Bean(destroyMethod = "shutdown")
    public DataSource dataSource() {
        log.debug("Configuring Datasource");
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(env, "spring.datasource.");
        if (propertyResolver.getProperty("url") == null && propertyResolver.getProperty("databaseName") == null) {
            log.error("Your database connection pool configuration is incorrect! The application" +
                    "cannot start. Please check your Spring profile, current profiles are: {}",
                    Arrays.toString(env.getActiveProfiles()));

            throw new ApplicationContextException("Database connection pool is not configured correctly");
        }
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(propertyResolver.getProperty("dataSourceClassName"));
        if (propertyResolver.getProperty("url") == null || "".equals(propertyResolver.getProperty("url"))) {
            config.addDataSourceProperty("databaseName", propertyResolver.getProperty("databaseName"));
            config.addDataSourceProperty("serverName", propertyResolver.getProperty("serverName"));
        } else {
            config.setJdbcUrl(propertyResolver.getProperty("url"));
        }
        config.setUsername(propertyResolver.getProperty("username"));
        config.setPassword(propertyResolver.getProperty("password"));

        if (metricRegistry != null) {
            config.setMetricRegistry(metricRegistry);
        }
        return new HikariDataSource(config);
    }
    
    /**
     * Configures the Hibernate JPA service with multi-tenant support enabled.
     * @param builder
     * @return
     */
    @PersistenceContext @Primary @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
    	RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(env, "spring.jpa.properties");
    	
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.multiTenancy", MultiTenancyStrategy.DATABASE.name());
        props.put("hibernate.multi_tenant_connection_provider", dsProvider);
        props.put("hibernate.tenant_identifier_resolver", tenantResolver);
        props.put("hibernate.id.new_generator_mappings", propertyResolver.getProperty("hibernate.id.new_generator_mappings"));
        props.put("hibernate.cache.use_second_level_cache", propertyResolver.getProperty("hibernate.cache.use_second_level_cache"));
        props.put("hibernate.cache.use_query_cache", propertyResolver.getProperty("hibernate.cache.use_query_cache"));
        props.put("hibernate.generate_statistics", propertyResolver.getProperty("hibernate.generate_statistics"));

        LocalContainerEntityManagerFactoryBean result = builder.dataSource(dataSource())
                .persistenceUnit("default")
                .properties(props)
                .packages("com.souzadriano").build();
        result.setJpaVendorAdapter(jpaVendorAdapter());
        return result;
    }
}
