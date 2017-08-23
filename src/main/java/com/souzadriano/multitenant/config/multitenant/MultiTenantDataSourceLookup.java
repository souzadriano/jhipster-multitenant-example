package com.souzadriano.multitenant.config.multitenant;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.lookup.MapDataSourceLookup;
import org.springframework.stereotype.Component;

import com.souzadriano.multitenant.domain.DataSourceConfig;
import com.souzadriano.multitenant.repository.DataSourceConfigRepository;
import com.zaxxer.hikari.HikariDataSource;

import io.github.jhipster.config.JHipsterConstants;
import io.github.jhipster.config.liquibase.AsyncSpringLiquibase;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;

@Component(value = "dataSourceLookup")
public class MultiTenantDataSourceLookup extends MapDataSourceLookup {

	private static final String DEFAULT_TENANTID = "default";
	private final Logger logger = LoggerFactory.getLogger(MultiTenantDataSourceLookup.class);

	@Autowired
	private ApplicationContext context;

	@Autowired
	private TaskExecutor taskExecutor;

	@Autowired
	private LiquibaseProperties liquibaseProperties;

	@Autowired
	private Environment environment;

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	public MultiTenantDataSourceLookup(HikariDataSource defaultDataSource, ApplicationContext context) {
		super();
		addDataSource(DEFAULT_TENANTID, defaultDataSource);
	}

	@EventListener
	public void handleContextRefresh(ContextRefreshedEvent event) throws LiquibaseException {
		DataSourceConfigRepository configRepository = context.getBean(DataSourceConfigRepository.class);
		addTenantDataSources(configRepository.findAll());
	}

	void addTenantDataSources(Collection<DataSourceConfig> dataSources) throws LiquibaseException {
		for (DataSourceConfig dataSource : dataSources) {
			// Add new datasource with own configuration per tenant
			HikariDataSource customDataSource = createTenantDataSource(dataSource);
			addDataSource(dataSource.getName(), customDataSource);
			liquibaseUpdate(customDataSource);
			logger.info("Configured tenant: " + dataSource.getName());
		}
	}

	private void liquibaseUpdate(HikariDataSource customDataSource) throws LiquibaseException {
		SpringLiquibase liquibase = new AsyncSpringLiquibase(taskExecutor, environment);
		liquibase.setResourceLoader(resourceLoader);
		liquibase.setDataSource(customDataSource);
		liquibase.setChangeLog("classpath:config/liquibase/master.xml");
		liquibase.setContexts(liquibaseProperties.getContexts());
		liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
		liquibase.setDropFirst(liquibaseProperties.isDropFirst());
		if (environment.acceptsProfiles(JHipsterConstants.SPRING_PROFILE_NO_LIQUIBASE)) {
			liquibase.setShouldRun(false);
		} else {
			liquibase.setShouldRun(liquibaseProperties.isEnabled());
			logger.debug("Configuring Liquibase");
		}
		liquibase.afterPropertiesSet();
	}

	private HikariDataSource createTenantDataSource(DataSourceConfig dataSource) {
		HikariDataSource customDataSource = new HikariDataSource();
		// url, username and password must be unique per tenant so there is not
		// default value
		customDataSource.setJdbcUrl(dataSource.getUrl());
		customDataSource.setUsername(dataSource.getUsername());
		customDataSource.setPassword(dataSource.getPassword());
		// These has default values in defaultDataSource
		// HikariDataSource defaultDataSource = (HikariDataSource)
		// getDataSource(DEFAULT_TENANTID);
		// customDataSource.setDriverClassName(defaultDataSource.getDriverClassName());

		// customDataSource.setIdleConnectionTestPeriodInMinutes(Long.valueOf(tenantProps.getProperty(
		// "database.idleConnectionTestPeriod",String.valueOf(defaultDataSource.getIdleConnectionTestPeriodInMinutes()))));
		// customDataSource.setIdleMaxAgeInMinutes(Long.valueOf(tenantProps.getProperty(
		// "database.idleMaxAge",
		// String.valueOf(defaultDataSource.getIdleMaxAgeInMinutes()))));
		// customDataSource.setMaxConnectionsPerPartition(Integer.valueOf(tenantProps.getProperty(
		// "database.maxConnectionsPerPartition",
		// String.valueOf(defaultDataSource.getMaxConnectionsPerPartition()))));
		// customDataSource.setMinConnectionsPerPartition(Integer.valueOf(tenantProps.getProperty(
		// "database.minConnectionsPerPartition",
		// String.valueOf(defaultDataSource.getMinConnectionsPerPartition()))));
		// customDataSource.setPartitionCount(Integer.valueOf(tenantProps.getProperty(
		// "database.partitionCount",
		// String.valueOf(defaultDataSource.getPartitionCount()))));
		// customDataSource.setAcquireIncrement(Integer.valueOf(tenantProps.getProperty(
		// "database.acquireIncrement",
		// String.valueOf(defaultDataSource.getAcquireIncrement()))));
		// customDataSource.setStatementsCacheSize(Integer.valueOf(tenantProps.getProperty(
		// "database.statementsCacheSize",String.valueOf(defaultDataSource.getStatementCacheSize()))));
		// customDataSource.setReleaseHelperThreads(Integer.valueOf(tenantProps.getProperty(
		// "database.releaseHelperThreads",
		// String.valueOf(defaultDataSource.getReleaseHelperThreads()))));customDataSource.setDriverClass(tenantProps.getProperty("database.driverClassName"));
		return customDataSource;
	}

}