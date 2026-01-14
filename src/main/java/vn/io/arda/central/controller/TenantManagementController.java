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
import vn.io.arda.central.service.TenantService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Management API for tenant administration.
 * Requires SUPER_ADMIN role for all operations.
 *
 * @since 0.0.2
 */
@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Allow CORS for local development
public class TenantManagementController {

    private final TenantService tenantService;
    private final TenantRepository tenantRepository;

    /**
     * Creates a new tenant.
     * Requires SUPER_ADMIN role.
     *
     * @param request Tenant creation request
     * @return Tenant creation response
     */
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
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
     * Requires SUPER_ADMIN role.
     *
     * @return List of tenant public information
     */
    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
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
     * Requires SUPER_ADMIN role.
     *
     * @param tenantKey Tenant key
     * @return Tenant public information
     */
    @GetMapping("/{tenantKey}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<TenantPublicInfoDto> getTenant(@PathVariable String tenantKey) {
        log.info("Fetching tenant details for: {}", tenantKey);

        TenantPublicInfoDto tenant = tenantService.getPublicInfo(tenantKey);

        return ResponseEntity.ok(tenant);
    }
}
