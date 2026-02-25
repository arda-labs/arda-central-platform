package vn.io.arda.central.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.io.arda.central.dto.*;
import vn.io.arda.central.service.MenuService;

import java.util.List;
import java.util.UUID;

/**
 * Menu management API.
 *
 * super_admin endpoints:
 * GET /v1/menus — list all master menus
 * POST /v1/menus — create master menu
 * PUT /v1/menus/{id} — update master menu
 * DELETE /v1/menus/{id} — delete master menu
 * GET /v1/menus/tenant/{tenantKey}/config — get tenant's full menu config
 * (admin view)
 * PUT /v1/menus/tenant/{tenantKey} — upsert tenant menu activation
 *
 * Public (authenticated) endpoint:
 * GET /v1/menus/tenant/{tenantKey} — get active menus for a tenant (used by
 * shell)
 *
 * @since 1.0
 */
@RestController
@RequestMapping("/v1/menus")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MenuController {

    private final MenuService menuService;

    // -------------------------------------------------------------------------
    // Master menu CRUD — super_admin only
    // -------------------------------------------------------------------------

    @GetMapping
    //@PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<List<MenuDto>> getAllMenus() {
        return ResponseEntity.ok(menuService.getAllMenus());
    }

    @GetMapping("/tree")
    //@PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<List<MenuDto>> getMenusTree() {
        return ResponseEntity.ok(menuService.getAllMenus());
    }

    @PostMapping
    //@PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<MenuDto> createMenu(@Valid @RequestBody MenuRequest request) {
        log.info("Creating menu: {}", request.getLabel());
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.createMenu(request));
    }

    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<MenuDto> updateMenu(
            @PathVariable UUID id,
            @Valid @RequestBody MenuRequest request) {
        log.info("Updating menu: {}", id);
        return ResponseEntity.ok(menuService.updateMenu(id, request));
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<Void> deleteMenu(@PathVariable UUID id) {
        log.warn("Deleting menu: {}", id);
        menuService.deleteMenu(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reorder")
    //@PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<Void> reorderMenus(@Valid @RequestBody List<MenuReorderRequest> request) {
        log.info("Reordering menus");
        menuService.reorderMenus(request);
        return ResponseEntity.ok().build();
    }

    // -------------------------------------------------------------------------
    // Tenant menu config — super_admin
    // -------------------------------------------------------------------------

    /**
     * Full config view for admin (includes disabled menus).
     */
    @GetMapping("/tenant/{tenantKey}/config")
    //@PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<List<TenantMenuDto>> getTenantMenuConfig(
            @PathVariable String tenantKey) {
        return ResponseEntity.ok(menuService.getTenantMenuConfig(tenantKey));
    }

    /**
     * Enable/disable a menu item for a tenant, with optional sort order override.
     */
    @PutMapping("/tenant/{tenantKey}")
    //@PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<TenantMenuDto> upsertTenantMenu(
            @PathVariable String tenantKey,
            @RequestBody TenantMenuRequest request) {
        log.info("Updating tenant menu config: tenant={}, menu={}", tenantKey, request.getMenuId());
        return ResponseEntity.ok(menuService.upsertTenantMenu(tenantKey, request));
    }

    // -------------------------------------------------------------------------
    // Public — shell uses this to load sidebar menus
    // -------------------------------------------------------------------------

    /**
     * Returns active menus for the given tenant.
     * Falls back to all master menus if tenant has no config yet.
     */
    @GetMapping("/tenant/{tenantKey}")
    public ResponseEntity<List<MenuDto>> getMenusForTenant(@PathVariable String tenantKey) {
        return ResponseEntity.ok(menuService.getMenusForTenant(tenantKey));
    }
}
