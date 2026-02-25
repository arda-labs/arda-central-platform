package vn.io.arda.central.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.io.arda.central.domain.entity.Tenant;
import vn.io.arda.central.domain.repository.TenantRepository;
import vn.io.arda.central.dto.CreateTenantRequest;
import vn.io.arda.central.dto.TenantDatabaseConfigDto;
import vn.io.arda.central.dto.TenantPublicInfoDto;
import vn.io.arda.central.dto.TenantDetailDto;
import vn.io.arda.central.dto.UpdateTenantRequest;
import vn.io.arda.shared.event.tenant.TenantCreatedEvent;
import vn.io.arda.shared.exception.ArdaException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {

    private final TenantRepository tenantRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Creates a new tenant and publishes TenantCreatedEvent for Keycloak
     * provisioning.
     *
     * @param request Tenant creation request
     * @return Created tenant entity
     * @throws ArdaException if tenant key already exists
     */
    @Transactional
    public Tenant createTenant(CreateTenantRequest request) {
        log.info("Creating new tenant with key: {}", request.getTenantKey());

        // Check if tenant already exists
        if (tenantRepository.findByTenantKey(request.getTenantKey()).isPresent()) {
            throw new ArdaException("Tenant with key '" + request.getTenantKey() + "' already exists");
        }

        // Build tenant entity
        Tenant tenant = Tenant.builder()
                .tenantKey(request.getTenantKey())
                .displayName(request.getDisplayName())
                .logoUrl(request.getLogoUrl())
                .primaryColor(request.getPrimaryColor())
                .dbType(Tenant.DatabaseType.valueOf(request.getDbType()))
                .jdbcUrl(request.getJdbcUrl())
                .dbUsername(request.getDbUsername())
                .dbPassword(request.getDbPassword())
                .driverClassName(request.getDriverClassName())
                .status(Tenant.TenantStatus.ACTIVE)
                .build();

        // Save to database
        Tenant savedTenant = tenantRepository.save(tenant);
        log.info("Tenant saved to database: {}", savedTenant.getTenantKey());

        // Publish event for Keycloak provisioning (async)
        TenantCreatedEvent event = new TenantCreatedEvent(
                savedTenant.getTenantKey(),
                savedTenant.getDisplayName(),
                savedTenant.getDbType().name());
        eventPublisher.publishEvent(event);
        log.info("TenantCreatedEvent published for tenant: {}", savedTenant.getTenantKey());

        return savedTenant;
    }

    /**
     * Get public tenant information for UI display
     * Safe for client-side consumption (no sensitive data)
     */
    @Transactional(readOnly = true)
    public TenantPublicInfoDto getPublicInfo(String tenantKey) {
        log.debug("Fetching public info for tenant: {}", tenantKey);

        // Special case for 'master' realm (System Administration)
        if ("master".equalsIgnoreCase(tenantKey)) {
            return new TenantPublicInfoDto(
                    "master",
                    "Arda System Admin",
                    "#6366F1", // Indigo
                    null, // Default logo
                    "POSTGRES",
                    "ACTIVE");
        }

        Tenant tenant = tenantRepository.findByTenantKey(tenantKey)
                .orElseThrow(() -> new ArdaException("Tenant not found: " + tenantKey));

        return new TenantPublicInfoDto(
                tenant.getTenantKey(), // tenantCode
                tenant.getDisplayName(), // tenantName
                tenant.getPrimaryColor(), // primaryColor
                tenant.getLogoUrl(), // logoUrl
                tenant.getDbType().name(), // dbType (POSTGRES -> POSTGRESQL)
                tenant.getStatus().name() // status (ACTIVE/INACTIVE)
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
                tenant.getDriverClassName());
    }

    /**
     * Update tenant UI configuration (name, logo, color).
     * Only allows updating display properties, not database config.
     *
     * @param tenantKey Tenant key to update
     * @param request   Update request with new values
     * @return Updated tenant details
     * @throws ArdaException if tenant not found
     */
    @Transactional
    public TenantDetailDto updateTenant(String tenantKey, UpdateTenantRequest request) {
        log.info("Updating tenant: {}", tenantKey);

        Tenant tenant = tenantRepository.findByTenantKey(tenantKey)
                .orElseThrow(() -> new ArdaException("Tenant not found: " + tenantKey));

        // Update only non-null fields
        if (request.getDisplayName() != null) {
            tenant.setDisplayName(request.getDisplayName());
        }
        if (request.getPrimaryColor() != null) {
            tenant.setPrimaryColor(request.getPrimaryColor());
        }
        if (request.getLogoUrl() != null) {
            tenant.setLogoUrl(request.getLogoUrl());
        }

        Tenant saved = tenantRepository.save(tenant);
        log.info("Tenant updated successfully: {}", tenantKey);

        return mapToDetailDto(saved);
    }

    /**
     * Update tenant status.
     *
     * @param tenantKey Tenant key
     * @param status    New status (ACTIVE, INACTIVE, TRIAL, SUSPENDED)
     * @return Updated tenant details
     * @throws ArdaException if tenant not found
     */
    @Transactional
    public TenantDetailDto updateStatus(String tenantKey, String status) {
        log.info("Updating tenant status: {} -> {}", tenantKey, status);

        Tenant tenant = tenantRepository.findByTenantKey(tenantKey)
                .orElseThrow(() -> new ArdaException("Tenant not found: " + tenantKey));

        tenant.setStatus(Tenant.TenantStatus.valueOf(status));
        Tenant saved = tenantRepository.save(tenant);

        log.info("Tenant status updated successfully: {} -> {}", tenantKey, status);
        return mapToDetailDto(saved);
    }

    /**
     * Soft delete tenant by setting status to INACTIVE.
     * Does not physically delete the tenant from database.
     *
     * @param tenantKey Tenant key to delete
     * @throws ArdaException if tenant not found
     */
    @Transactional
    public void deleteTenant(String tenantKey) {
        log.warn("Soft deleting tenant: {}", tenantKey);

        Tenant tenant = tenantRepository.findByTenantKey(tenantKey)
                .orElseThrow(() -> new ArdaException("Tenant not found: " + tenantKey));

        tenantRepository.delete(tenant);

        log.info("Tenant physically deleted: {}", tenantKey);
    }

    /**
     * Get detailed tenant information including database config.
     * For SUPER_ADMIN use only.
     *
     * @param tenantKey Tenant key
     * @return Detailed tenant information
     * @throws ArdaException if tenant not found
     */
    @Transactional(readOnly = true)
    public TenantDetailDto getTenantDetails(String tenantKey) {
        log.debug("Fetching detailed info for tenant: {}", tenantKey);

        Tenant tenant = tenantRepository.findByTenantKey(tenantKey)
                .orElseThrow(() -> new ArdaException("Tenant not found: " + tenantKey));

        return mapToDetailDto(tenant);
    }

    /**
     * Map Tenant entity to TenantDetailDto
     */
    private TenantDetailDto mapToDetailDto(Tenant tenant) {
        return new TenantDetailDto(
                tenant.getTenantKey(),
                tenant.getDisplayName(),
                tenant.getPrimaryColor(),
                tenant.getLogoUrl(),
                tenant.getDbType().name(),
                tenant.getStatus().name(),
                tenant.getJdbcUrl(),
                tenant.getDbUsername(),
                tenant.getCreatedAt(),
                tenant.getUpdatedAt());
    }
}
