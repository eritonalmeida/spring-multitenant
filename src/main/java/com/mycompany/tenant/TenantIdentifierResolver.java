package com.mycompany.tenant;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    public static final String DEFAULT_TENANT_ID = "auth";

    @Override
    public String resolveCurrentTenantIdentifier() {

        String currentTenantId = TenantContext.getTenantId();

        if (currentTenantId != null) {
            return currentTenantId;

        }

        return DEFAULT_TENANT_ID;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
