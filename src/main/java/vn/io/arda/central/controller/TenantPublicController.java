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
@RequestMapping("/api/v1/public/tenants")
@RequiredArgsConstructor
@Slf4j
public class TenantPublicController {

    private final TenantService tenantService;

    /**
     * Get public tenant information by tenant key
     * Returns UI configuration (name, logo, colors) for login screen decoration
     *
     * @param tenantKey Unique tenant identifier
     * @return Tenant public information (no sensitive database configuration)
     */
    @GetMapping("/info/{tenantKey}")
    public ResponseEntity<TenantPublicInfoDto> getTenantInfo(@PathVariable String tenantKey) {
        log.info("Public API: Fetching tenant info for: {}", tenantKey);

        TenantPublicInfoDto tenantInfo = tenantService.getPublicInfo(tenantKey);

        return ResponseEntity.ok(tenantInfo);
    }
}
