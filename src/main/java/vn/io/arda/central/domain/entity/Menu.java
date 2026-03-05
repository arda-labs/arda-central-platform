package vn.io.arda.central.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Master menu entity stored in arda_central DB.
 * SUPER_ADMIN manages the global menu catalog here.
 * Supports hierarchical structure (parent/children) for nested menus.
 *
 * @since 1.0
 */
@Entity
@Table(name = "menus", indexes = {
    @Index(name = "idx_menu_parent_id", columnList = "parent_id"),
    @Index(name = "idx_menu_sort_order", columnList = "sort_order")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @NotBlank(message = "Label is required")
    @Column(name = "label", nullable = false, length = 100)
    private String label;

    /**
     * Frontend route path (e.g., "/crm/leads").
     * Null for group/section headers.
     */
    @Column(name = "path", length = 255)
    private String path;

    /**
     * Lucide icon name (e.g., "Users", "BarChart2").
     */
    @Column(name = "icon", length = 100)
    private String icon;

    /**
     * Tailwind color class or hex for icon (e.g., "#10B981").
     */
    @Column(name = "icon_color", length = 50)
    private String iconColor;

    /**
     * Display order within same parent level.
     */
    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    /**
     * Parent menu for nested structure.
     * Null = top-level item.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Menu parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY,
               cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<Menu> children = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
