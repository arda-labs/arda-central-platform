package vn.io.arda.central.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.io.arda.central.domain.entity.Tenant;
import vn.io.arda.central.domain.repository.TenantRepository;
import vn.io.arda.central.dto.TenantDatabaseConfigDto;
import vn.io.arda.central.dto.TenantPublicInfoDto;
import vn.io.arda.shared.exception.ArdaException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TenantService {

    private final TenantRepository tenantRepository;

    /**
     * Get public tenant information for UI display
     * Safe for client-side consumption (no sensitive data)
     */
    public TenantPublicInfoDto getPublicInfo(String tenantKey) {
        log.debug("Fetching public info for tenant: {}", tenantKey);

        Tenant tenant = tenantRepository.findByTenantKey(tenantKey)
            .orElseThrow(() -> new ArdaException("Tenant not found: " + tenantKey));

        return new TenantPublicInfoDto(
            tenant.getTenantKey(),
            tenant.getDisplayName(),
            tenant.getStatus().name(),
            tenant.getLogoUrl(),
            tenant.getPrimaryColor()
        );
    }

    /**
     * Get tenant database configuration for internal services
     * Contains sensitive information - should only be called by internal services
     */
    public TenantDatabaseConfigDto getDatabaseConfig(String tenantKey) {
        log.debug("Fetching database config for tenant: {}", tenantKey);

        Tenant tenant = tenantRepository.findByTenantKey(tenantKey)
            .orElseThrow(() -> new ArdaException("Tenant configuration not found: " + tenantKey));

        return new TenantDatabaseConfigDto(
            tenant.getTenantKey(),
            tenant.getDisplayName(),
            tenant.getStatus().name(),
            tenant.getDbType().name(),
            tenant.getJdbcUrl(),
            tenant.getDbUsername(),
            tenant.getDbPassword(),
            tenant.getDriverClassName()
        );
    }
}
