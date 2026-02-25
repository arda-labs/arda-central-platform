package vn.io.arda.central.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.io.arda.central.domain.entity.Menu;
import vn.io.arda.central.domain.entity.TenantMenu;
import vn.io.arda.central.dto.MenuDto;
import vn.io.arda.central.dto.MenuReorderRequest;
import vn.io.arda.central.dto.MenuRequest;
import vn.io.arda.central.dto.TenantMenuDto;
import vn.io.arda.central.dto.TenantMenuRequest;
import vn.io.arda.central.repository.MenuRepository;
import vn.io.arda.central.repository.TenantMenuRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {

    private final MenuRepository menuRepository;
    private final TenantMenuRepository tenantMenuRepository;

    // -------------------------------------------------------------------------
    // Master menu CRUD (SUPER_ADMIN)
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<MenuDto> getAllMenus() {
        return menuRepository.findAllRootMenus().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MenuDto createMenu(MenuRequest request) {
        Menu parent = null;
        if (request.getParentId() != null) {
            parent = menuRepository.findById(request.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Parent menu not found: " + request.getParentId()));
        }

        Menu menu = Menu.builder()
                .label(request.getLabel())
                .path(request.getPath())
                .icon(request.getIcon())
                .iconColor(request.getIconColor())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .parent(parent)
                .build();

        return toDto(menuRepository.save(menu));
    }

    @Transactional
    public MenuDto updateMenu(UUID id, MenuRequest request) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Menu not found: " + id));

        menu.setLabel(request.getLabel());
        menu.setPath(request.getPath());
        menu.setIcon(request.getIcon());
        menu.setIconColor(request.getIconColor());
        if (request.getSortOrder() != null) {
            menu.setSortOrder(request.getSortOrder());
        }

        if (request.getParentId() != null) {
            Menu parent = menuRepository.findById(request.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Parent menu not found: " + request.getParentId()));
            menu.setParent(parent);
        } else {
            menu.setParent(null);
        }

        return toDto(menuRepository.save(menu));
    }

    @Transactional
    public void deleteMenu(UUID id) {
        if (!menuRepository.existsById(id)) {
            throw new EntityNotFoundException("Menu not found: " + id);
        }
        menuRepository.deleteById(id);
    }

    @Transactional
    public void reorderMenus(List<MenuReorderRequest> requestItems) {
        for (MenuReorderRequest item : requestItems) {
            Menu menu = menuRepository.findById(item.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Menu not found: " + item.getId()));

            if (item.getParentId() != null) {
                Menu parent = menuRepository.findById(item.getParentId())
                        .orElseThrow(() -> new EntityNotFoundException("Parent menu not found: " + item.getParentId()));
                menu.setParent(parent);
            } else {
                menu.setParent(null);
            }

            if (item.getSortOrder() != null) {
                menu.setSortOrder(item.getSortOrder());
            }

            menuRepository.save(menu);
        }
    }

    // -------------------------------------------------------------------------
    // Tenant menu config
    // -------------------------------------------------------------------------

    /**
     * Get active menus for a tenant.
     * If no config exists yet, falls back to all master root menus
     * (default-all-enabled behavior).
     */
    @Transactional(readOnly = true)
    public List<MenuDto> getMenusForTenant(String tenantKey) {
        if (!tenantMenuRepository.existsByTenantKey(tenantKey)) {
            // Tenant hasn't configured menus yet → return all master menus
            log.debug("No menu config for tenant {}, returning all master menus", tenantKey);
            return menuRepository.findAllRootMenus().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        }

        return tenantMenuRepository.findEnabledRootMenusForTenant(tenantKey).stream()
                .map(tm -> toDtoWithOverride(tm))
                .collect(Collectors.toList());
    }

    /**
     * Get full config list for a tenant (admin view — enabled + disabled).
     */
    @Transactional(readOnly = true)
    public List<TenantMenuDto> getTenantMenuConfig(String tenantKey) {
        return tenantMenuRepository.findByTenantKeyOrderByMenuSortOrderAsc(tenantKey).stream()
                .map(this::toTenantMenuDto)
                .collect(Collectors.toList());
    }

    /**
     * Upsert a tenant-menu config entry.
     */
    @Transactional
    public TenantMenuDto upsertTenantMenu(String tenantKey, TenantMenuRequest request) {
        Menu menu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Menu not found: " + request.getMenuId()));

        TenantMenu tenantMenu = tenantMenuRepository
                .findByTenantKeyAndMenuId(tenantKey, menu.getId())
                .orElseGet(() -> TenantMenu.builder()
                        .tenantKey(tenantKey)
                        .menu(menu)
                        .build());

        if (request.getEnabled() != null) {
            tenantMenu.setEnabled(request.getEnabled());
        }
        tenantMenu.setSortOrderOverride(request.getSortOrderOverride());

        return toTenantMenuDto(tenantMenuRepository.save(tenantMenu));
    }

    // -------------------------------------------------------------------------
    // Mappers
    // -------------------------------------------------------------------------

    private MenuDto toDto(Menu menu) {
        List<MenuDto> children = menu.getChildren() == null ? null
                : menu.getChildren().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList());

        return MenuDto.builder()
                .id(menu.getId())
                .label(menu.getLabel())
                .path(menu.getPath())
                .icon(menu.getIcon())
                .iconColor(menu.getIconColor())
                .sortOrder(menu.getSortOrder())
                .children(children)
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .build();
    }

    private MenuDto toDtoWithOverride(TenantMenu tm) {
        MenuDto dto = toDto(tm.getMenu());
        if (tm.getSortOrderOverride() != null) {
            dto.setSortOrder(tm.getSortOrderOverride());
        }
        return dto;
    }

    private TenantMenuDto toTenantMenuDto(TenantMenu tm) {
        return TenantMenuDto.builder()
                .id(tm.getId())
                .tenantKey(tm.getTenantKey())
                .menu(toDto(tm.getMenu()))
                .enabled(tm.getEnabled())
                .sortOrderOverride(tm.getSortOrderOverride())
                .build();
    }
}
