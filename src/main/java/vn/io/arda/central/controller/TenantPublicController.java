package vn.io.arda.central.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.io.arda.central.dto.TenantPublicInfoDto;
import vn.io.arda.central.service.TenantService;

/**
 * Public API for tenant information
 * Provides UI configuration and branding information
 * Safe for client-side consumption
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Allow CORS for local development
public class TenantPublicController {

    private final TenantService tenantService;

    /**
     * Get public tenant information by tenant key
     * Returns UI configuration (name, logo, colors) for login screen decoration
     *
     * @param tenantKey Unique tenant identifier
     * @return Tenant public information (no sensitive database configuration)
     */
    @GetMapping("/public/tenants/info/{tenantKey}")
    public ResponseEntity<TenantPublicInfoDto> getTenantInfo(@PathVariable String tenantKey) {
        log.info("Public API: Fetching tenant info for: {}", tenantKey);

        TenantPublicInfoDto tenantInfo = tenantService.getPublicInfo(tenantKey);

        return ResponseEntity.ok(tenantInfo);
    }

    /**
     * Get tenant information by tenant code (simplified endpoint for frontend)
     * Same as /public/tenants/info/{tenantCode} but with shorter path
     *
     * @param tenantCode Unique tenant identifier
     * @return Tenant public information
     */
    @GetMapping("/tenants/{tenantCode}")
    public ResponseEntity<TenantPublicInfoDto> getTenantByCode(@PathVariable String tenantCode) {
        log.info("Public API: Fetching tenant by code: {}", tenantCode);

        TenantPublicInfoDto tenantInfo = tenantService.getPublicInfo(tenantCode);

        return ResponseEntity.ok(tenantInfo);
    }
}
