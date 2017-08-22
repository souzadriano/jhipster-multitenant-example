package com.souzadriano.multitenant.config.multitenant;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.stereotype.Component;

/**
 * It gets the connection based on different datasources. 
 */
@Component("multiTenantConnectionProvider")
public class MultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {
	
	private static final long serialVersionUID = -2273003146102196007L;

	private final Logger logger = LoggerFactory.getLogger(MultiTenantConnectionProviderImpl.class);
	
    @Autowired
    private DataSource defaultDataSource;
    
    @Autowired
    private DataSourceLookup dataSourceLookup;
    
    /**
     * Select datasources in situations where not tenantId is used (e.g. startup processing).
    */
    @Override
    protected DataSource selectAnyDataSource() {
        logger.debug("Select any dataSource: " + defaultDataSource);
        return defaultDataSource;
    }
    
    /**
     * Obtains a DataSource based on tenantId
    */
    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        DataSource ds = dataSourceLookup.getDataSource(tenantIdentifier);
        logger.debug("Select dataSource from "+ tenantIdentifier+ ": " + ds);
        return ds;
    }
}