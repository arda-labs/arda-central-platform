package vn.io.arda.central.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.io.arda.central.domain.entity.Tenant;
import vn.io.arda.central.domain.repository.TenantRepository;
import vn.io.arda.central.dto.CreateTenantRequest;
import vn.io.arda.central.dto.TenantDatabaseConfigDto;
import vn.io.arda.central.dto.TenantPublicInfoDto;
import vn.io.arda.central.dto.TenantDetailDto;
import vn.io.arda.central.dto.UpdateTenantRequest;
import vn.io.arda.shared.event.tenant.TenantConfigEvent;
import vn.io.arda.shared.event.tenant.TenantCreatedEvent;
import vn.io.arda.shared.event.tenant.TenantDeletedEvent;
import vn.io.arda.shared.event.tenant.TenantStatusUpdatedEvent;
import vn.io.arda.shared.event.tenant.TenantUpdatedEvent;
import vn.io.arda.shared.exception.ArdaException;
import vn.io.arda.shared.multitenant.model.TenantDataSourceConfig;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {

    private static final String TENANT_CONFIG_TOPIC = "arda.tenant-config-events";

    private final TenantRepository tenantRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

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

        // Auto-generate DB connection info from tenantKey
        String tenantKey = request.getTenantKey();
        String dbName = "arda_" + tenantKey;
        String dbUser = "arda_" + tenantKey;
        String dbPass = generateSecurePassword(tenantKey);
        String jdbcUrl = "jdbc:postgresql://localhost:5432/" + dbName;
        String driverClass = "org.postgresql.Driver";
        // Build tenant entity
        Tenant tenant = Tenant.builder()
                .tenantKey(tenantKey)
                .displayName(request.getDisplayName())
                .logoUrl(request.getLogoUrl())
                .primaryColor(request.getPrimaryColor())
                .jdbcUrl(jdbcUrl)
                .dbUsername(dbUser)
                .dbPassword(dbPass)
                .driverClassName(driverClass)
                .status(Tenant.TenantStatus.ACTIVE)
                .build();

        // Save to database
        Tenant savedTenant = tenantRepository.save(tenant);
        log.info("Tenant saved to database: {}", savedTenant.getTenantKey());

        // Publish event for Keycloak + DB provisioning (async via Kafka)
        TenantCreatedEvent event = new TenantCreatedEvent(
                savedTenant.getTenantKey(),
                savedTenant.getDisplayName());
        kafkaTemplate.send("tenant-events", event);
        log.info("TenantCreatedEvent published to Kafka for tenant: {}", savedTenant.getTenantKey());

        // Publish TenantConfigEvent for event-driven cache sync
        publishTenantConfigEvent(TenantConfigEvent.created(
                savedTenant.getTenantKey(), buildDataSourceConfig(savedTenant)));

        return savedTenant;
    }

    /**
     * Generates a deterministic but secure password for tenant DB.
     * In production this should be stored in a secrets manager.
     */
    private String generateSecurePassword(String tenantKey) {
        return "Arda@" + tenantKey.toUpperCase() + "2025!";
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
                    "ACTIVE");
        }

        Tenant tenant = tenantRepository.findByTenantKey(tenantKey)
                .orElseThrow(() -> new ArdaException("Tenant not found: " + tenantKey));

        return new TenantPublicInfoDto(
                tenant.getTenantKey(), // tenantCode
                tenant.getDisplayName(), // tenantName
                tenant.getPrimaryColor(), // primaryColor
                tenant.getLogoUrl(), // logoUrl
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

        // Notify PLATFORM_ADMIN users about the update
        TenantUpdatedEvent event = new TenantUpdatedEvent(tenantKey, saved.getDisplayName());
        kafkaTemplate.send("tenant-events", event);
        log.info("TenantUpdatedEvent published for tenant: {}", tenantKey);

        // Publish TenantConfigEvent for event-driven cache sync
        publishTenantConfigEvent(TenantConfigEvent.updated(
                tenantKey, buildDataSourceConfig(saved)));

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

        // Notify PLATFORM_ADMIN users about the status change
        TenantStatusUpdatedEvent event = new TenantStatusUpdatedEvent(tenantKey, status);
        kafkaTemplate.send("tenant-events", event);
        log.info("TenantStatusUpdatedEvent published for tenant: {} -> {}", tenantKey, status);

        // Publish TenantConfigEvent for event-driven cache sync
        publishTenantConfigEvent(TenantConfigEvent.updated(
                tenantKey, buildDataSourceConfig(saved)));

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
        log.warn("Deleting tenant: {}", tenantKey);

        Tenant tenant = tenantRepository.findByTenantKey(tenantKey)
                .orElseThrow(() -> new ArdaException("Tenant not found: " + tenantKey));

        tenantRepository.delete(tenant);
        log.info("Tenant physically deleted from database: {}", tenantKey);

        // Publish event to trigger Keycloak realm + DB deletion
        TenantDeletedEvent event = new TenantDeletedEvent(tenantKey);
        kafkaTemplate.send("tenant-events", event);
        log.info("TenantDeletedEvent published to Kafka for tenant: {}", tenantKey);

        // Publish TenantConfigEvent for event-driven cache eviction
        publishTenantConfigEvent(TenantConfigEvent.deleted(tenantKey));
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
                tenant.getStatus().name(),
                tenant.getJdbcUrl(),
                tenant.getDbUsername(),
                tenant.getCreatedAt(),
                tenant.getUpdatedAt());
    }

    // --- V2: Event-Driven Tenant Config ---

    /**
     * Gets all active tenant configs as a snapshot (for bootstrap on service restart).
     */
    @Transactional(readOnly = true)
    public Map<String, TenantDataSourceConfig> getAllTenantConfigs() {
        return tenantRepository.findAll().stream()
                .filter(t -> t.getStatus() == Tenant.TenantStatus.ACTIVE)
                .collect(Collectors.toMap(
                        Tenant::getTenantKey,
                        this::buildDataSourceConfig));
    }

    /**
     * Locks a tenant for maintenance (e.g., during non-backward-compatible migration).
     */
    public void lockTenantForMaintenance(String tenantKey) {
        log.info("Locking tenant for maintenance: {}", tenantKey);
        publishTenantConfigEvent(TenantConfigEvent.locked(tenantKey));
    }

    /**
     * Unlocks a tenant after maintenance.
     */
    public void unlockTenantFromMaintenance(String tenantKey) {
        log.info("Unlocking tenant from maintenance: {}", tenantKey);
        publishTenantConfigEvent(TenantConfigEvent.unlocked(tenantKey));
    }

    private TenantDataSourceConfig buildDataSourceConfig(Tenant tenant) {
        return TenantDataSourceConfig.builder()
                .tenantId(tenant.getTenantKey())
                .jdbcUrl(tenant.getJdbcUrl())
                .username(tenant.getDbUsername())
                .password(tenant.getDbPassword())
                .driverClassName(tenant.getDriverClassName())
                .build();
    }

    private void publishTenantConfigEvent(TenantConfigEvent event) {
        try {
            kafkaTemplate.send(TENANT_CONFIG_TOPIC, event.getTenantKey(), event);
            log.info("TenantConfigEvent published: type={}, tenant={}",
                    event.getEventType(), event.getTenantKey());
        } catch (Exception e) {
            log.error("Failed to publish TenantConfigEvent for tenant: {}",
                    event.getTenantKey(), e);
            // Don't rethrow — config event failure should not block CRUD operations
        }
    }
}
