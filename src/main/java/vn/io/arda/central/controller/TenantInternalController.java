package vn.io.arda.central.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.io.arda.central.dto.TenantDatabaseConfigDto;
import vn.io.arda.central.service.TenantService;
import vn.io.arda.shared.multitenant.model.TenantDataSourceConfig;

import java.util.Map;

/**
 * Internal API for tenant database configuration.
 * Provides database connection information for internal services.
 * SHOULD BE PROTECTED by API Gateway or mTLS in production.
 */
@RestController
@RequestMapping("/v1/internal/tenants")
@RequiredArgsConstructor
@Slf4j
public class TenantInternalController {

    private final TenantService tenantService;

    /**
     * Get tenant database configuration by tenant key.
     * Returns full database connection details for service initialization.
     *
     * WARNING: Contains sensitive information.
     * Should only be called by internal services (CRM, BPM, IAM).
     */
    @GetMapping("/config/{tenantKey}")
    public ResponseEntity<TenantDatabaseConfigDto> getTenantDatabaseConfig(@PathVariable String tenantKey) {
        log.info("Internal API: Fetching database config for tenant: {}", tenantKey);
        TenantDatabaseConfigDto config = tenantService.getDatabaseConfig(tenantKey);
        return ResponseEntity.ok(config);
    }

    /**
     * Bootstrap snapshot: returns all active tenant configs.
     * Used by services in event-driven mode to hydrate their local cache on startup.
     */
    @GetMapping("/configs")
    public ResponseEntity<Map<String, TenantDataSourceConfig>> getAllTenantConfigs() {
        log.info("Internal API: Fetching all tenant configs (bootstrap snapshot)");
        return ResponseEntity.ok(tenantService.getAllTenantConfigs());
    }

    /**
     * Lock a tenant for maintenance (non-backward-compatible migration).
     * Publishes LOCKED event -> downstream services return 503 for this tenant.
     */
    @PostMapping("/{tenantKey}/lock")
    public ResponseEntity<Void> lockTenant(@PathVariable String tenantKey) {
        tenantService.lockTenantForMaintenance(tenantKey);
        return ResponseEntity.ok().build();
    }

    /**
     * Unlock a tenant after maintenance.
     * Publishes UNLOCKED event -> downstream services resume serving this tenant.
     */
    @PostMapping("/{tenantKey}/unlock")
    public ResponseEntity<Void> unlockTenant(@PathVariable String tenantKey) {
        tenantService.unlockTenantFromMaintenance(tenantKey);
        return ResponseEntity.ok().build();
    }
}
