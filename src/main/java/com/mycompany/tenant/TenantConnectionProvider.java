package com.mycompany.tenant;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Value("${spring.datasource.driver-class-name}")
    private String datasourceDriver;

    private Map<String, DataSource> dataSources = new HashMap<>();

    @PostConstruct
    public void init() {
        var factory = DataSourceBuilder.create()
                .url(datasourceUrl)
                .username(datasourceUsername)
                .password(datasourcePassword)
                .driverClassName(datasourceDriver);

        dataSources.put(TenantIdentifierResolver.DEFAULT_TENANT_ID, factory.build());
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return dataSources.values().iterator().next();
    }

    @Override
    protected DataSource selectDataSource(String tenantId) {
        DataSource ds = dataSources.get(tenantId);

        if (ds != null) {
            return ds;
        }

        var factory = DataSourceBuilder.create()
                .url("jdbc:sqlite:sqlite/" + tenantId + ".db")
                .username(datasourceUsername)
                .password(datasourcePassword)
                .driverClassName(datasourceDriver);

        ds = factory.build();

        dataSources.put(tenantId, ds);

        return ds;
    }
}
