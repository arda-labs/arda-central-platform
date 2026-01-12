package vn.io.arda.central.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.io.arda.central.dto.TenantDatabaseConfigDto;
import vn.io.arda.central.service.TenantService;

/**
 * Internal API for tenant database configuration
 * Provides database connection information for internal services
 * SHOULD BE PROTECTED by API Gateway or mTLS in production
 */
@RestController
@RequestMapping("/api/v1/internal/tenants")
@RequiredArgsConstructor
@Slf4j
public class TenantInternalController {

    private final TenantService tenantService;

    /**
     * Get tenant database configuration by tenant key
     * Returns full database connection details for service initialization
     *
     * WARNING: Contains sensitive information
     * Should only be called by internal services (CRM, BPM, IAM)
     *
     * @param tenantKey Unique tenant identifier
     * @return Complete database configuration including credentials
     */
    @GetMapping("/config/{tenantKey}")
    public ResponseEntity<TenantDatabaseConfigDto> getTenantDatabaseConfig(@PathVariable String tenantKey) {
        log.info("Internal API: Fetching database config for tenant: {}", tenantKey);

        TenantDatabaseConfigDto config = tenantService.getDatabaseConfig(tenantKey);

        return ResponseEntity.ok(config);
    }
}
