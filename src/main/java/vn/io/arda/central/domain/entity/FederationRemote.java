package vn.io.arda.central.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "federation_remotes", indexes = {
    @Index(name = "idx_federation_name", columnList = "name", unique = true),
    @Index(name = "idx_federation_enabled", columnList = "enabled"),
    @Index(name = "idx_federation_tenant", columnList = "tenant_key"),
    @Index(name = "idx_federation_order", columnList = "display_order")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FederationRemote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Module name is required")
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @NotBlank(message = "Display name is required")
    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "description", length = 500)
    private String description;

    @NotBlank(message = "Remote entry URL is required")
    @Column(name = "remote_entry_url", nullable = false, length = 500)
    private String remoteEntryUrl;

    @Column(name = "exposed_module", length = 100)
    @Builder.Default
    private String exposedModule = "./Component";

    @Column(name = "icon", length = 50)
    private String icon;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "tenant_key", length = 50)
    private String tenantKey;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
