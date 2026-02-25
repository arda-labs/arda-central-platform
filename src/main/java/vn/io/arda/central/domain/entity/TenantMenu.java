package vn.io.arda.central.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Tenant-specific menu activation.
 * Each tenant selects which master menus they want to display.
 * Stored in arda_central DB (not per-tenant).
 *
 * @since 1.0
 */
@Entity
@Table(name = "tenant_menus",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_tenant_menu",
        columnNames = {"tenant_key", "menu_id"}
    ),
    indexes = {
        @Index(name = "idx_tenant_menu_tenant", columnList = "tenant_key"),
        @Index(name = "idx_tenant_menu_enabled", columnList = "enabled")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The tenant this configuration belongs to.
     */
    @Column(name = "tenant_key", nullable = false, length = 6)
    private String tenantKey;

    /**
     * The master menu item being configured.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    /**
     * Whether this menu is visible for the tenant.
     */
    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    /**
     * Optional sort order override per tenant.
     * Null = use the master menu's sort order.
     */
    @Column(name = "sort_order_override")
    private Integer sortOrderOverride;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
