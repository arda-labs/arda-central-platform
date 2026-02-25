package vn.io.arda.central.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.io.arda.central.domain.entity.TenantMenu;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantMenuRepository extends JpaRepository<TenantMenu, UUID> {

    /**
     * All enabled tenant-menu configs for a given tenant.
     */
    @Query("SELECT tm FROM TenantMenu tm JOIN FETCH tm.menu m " +
           "LEFT JOIN FETCH m.children " +
           "WHERE tm.tenantKey = :tenantKey AND tm.enabled = true " +
           "AND m.parent IS NULL " +
           "ORDER BY COALESCE(tm.sortOrderOverride, m.sortOrder) ASC")
    List<TenantMenu> findEnabledRootMenusForTenant(@Param("tenantKey") String tenantKey);

    /**
     * All configs for a tenant (enabled + disabled), for management UI.
     */
    List<TenantMenu> findByTenantKeyOrderByMenuSortOrderAsc(String tenantKey);

    /**
     * Find specific tenant-menu config.
     */
    Optional<TenantMenu> findByTenantKeyAndMenuId(String tenantKey, UUID menuId);

    /**
     * Check if tenant has any menu config (i.e. was set up before).
     */
    boolean existsByTenantKey(String tenantKey);
}
