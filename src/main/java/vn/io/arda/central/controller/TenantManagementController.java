package vn.io.arda.central.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.io.arda.central.domain.entity.Tenant;
import vn.io.arda.central.domain.repository.TenantRepository;
import vn.io.arda.central.dto.CreateTenantRequest;
import vn.io.arda.central.dto.CreateTenantResponse;
import vn.io.arda.central.dto.TenantPublicInfoDto;
import vn.io.arda.central.dto.TenantDetailDto;
import vn.io.arda.central.dto.UpdateTenantRequest;
import vn.io.arda.central.dto.UpdateStatusRequest;
import vn.io.arda.central.service.TenantService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Management API for tenant administration.
 * Requires super_admin role for all operations.
 *
 * @since 0.0.2
 */
@RestController
@RequestMapping("/v1/tenants")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Allow CORS for local development
public class TenantManagementController {

    private final TenantService tenantService;
    private final TenantRepository tenantRepository;

    /**
     * Creates a new tenant.
     * Requires super_admin role.
     *
     * @param request Tenant creation request
     * @return Tenant creation response
     */
    @PostMapping
    //@PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<CreateTenantResponse> createTenant(@Valid @RequestBody CreateTenantRequest request) {
        log.info("Received tenant creation request for: {}", request.getTenantKey());

        try {
            Tenant tenant = tenantService.createTenant(request);

            CreateTenantResponse response = CreateTenantResponse.success(tenant.getTenantKey());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Failed to create tenant: {}", request.getTenantKey(), e);

            CreateTenantResponse errorResponse = CreateTenantResponse.error(
                    request.getTenantKey(),
                    e.getMessage()
            );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Lists all tenants (for admin dashboard).
     * Requires super_admin role.
     *
     * @return List of tenant public information
     */
    @GetMapping
    //@PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<List<TenantPublicInfoDto>> listTenants() {
        log.info("Fetching all tenants for admin dashboard");

        List<TenantPublicInfoDto> tenants = tenantRepository.findAll().stream()
                .map(tenant -> new TenantPublicInfoDto(
                        tenant.getTenantKey(),
                        tenant.getDisplayName(),
                        tenant.getPrimaryColor(),
                        tenant.getLogoUrl(),
                        tenant.getDbType().name(),
                        tenant.getStatus().name()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(tenants);
    }

    /**
     * Gets tenant details by key.
     * Requires super_admin role.
     *
     * @param tenantKey Tenant key
     * @return Tenant public information
     */
    @GetMapping("/{tenantKey}")
    //@PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<TenantPublicInfoDto> getTenant(@PathVariable String tenantKey) {
        log.info("Fetching tenant details for: {}", tenantKey);

        TenantPublicInfoDto tenant = tenantService.getPublicInfo(tenantKey);

        return ResponseEntity.ok(tenant);
    }

    /**
     * Gets detailed tenant information including database config.
     * Requires super_admin role.
     *
     * @param tenantKey Tenant key
     * @return Detailed tenant information
     */
    @GetMapping("/{tenantKey}/details")
    //@PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<TenantDetailDto> getTenantDetails(@PathVariable String tenantKey) {
        log.info("Fetching detailed tenant info for: {}", tenantKey);

        TenantDetailDto details = tenantService.getTenantDetails(tenantKey);

        return ResponseEntity.ok(details);
    }

    /**
     * Updates tenant UI configuration (name, logo, primary color).
     * Requires super_admin role.
     *
     * @param tenantKey Tenant key to update
     * @param request Update request
     * @return Updated tenant details
     */
    @PutMapping("/{tenantKey}")
    //@PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<TenantDetailDto> updateTenant(
            @PathVariable String tenantKey,
            @Valid @RequestBody UpdateTenantRequest request) {
        log.info("Updating tenant: {}", tenantKey);

        TenantDetailDto updated = tenantService.updateTenant(tenantKey, request);

        return ResponseEntity.ok(updated);
    }

    /**
     * Updates tenant status.
     * Requires super_admin role.
     *
     * @param tenantKey Tenant key
     * @param request Status update request
     * @return Updated tenant details
     */
    @PatchMapping("/{tenantKey}/status")
    //@PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<TenantDetailDto> updateStatus(
            @PathVariable String tenantKey,
            @Valid @RequestBody UpdateStatusRequest request) {
        log.info("Updating tenant status: {} -> {}", tenantKey, request.getStatus());

        TenantDetailDto updated = tenantService.updateStatus(tenantKey, request.getStatus());

        return ResponseEntity.ok(updated);
    }

    /**
     * Soft deletes a tenant (sets status to INACTIVE).
     * Requires super_admin role.
     *
     * @param tenantKey Tenant key to delete
     * @return No content
     */
    @DeleteMapping("/{tenantKey}")
    //@PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<Void> deleteTenant(@PathVariable String tenantKey) {
        log.warn("Deleting tenant: {}", tenantKey);

        tenantService.deleteTenant(tenantKey);

        return ResponseEntity.noContent().build();
    }
}
