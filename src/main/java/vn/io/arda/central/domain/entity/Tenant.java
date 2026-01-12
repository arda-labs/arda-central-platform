package vn.io.arda.central.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tenants", indexes = {
    @Index(name = "idx_tenant_key", columnList = "tenant_key", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Tenant key is required")
    @Pattern(regexp = "^[a-z0-9_-]+$", message = "Tenant key must contain only lowercase letters, numbers, hyphens, and underscores")
    @Column(name = "tenant_key", nullable = false, unique = true, length = 50)
    private String tenantKey;

    @NotBlank(message = "Display name is required")
    @Column(name = "display_name", nullable = false, length = 255)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private TenantStatus status = TenantStatus.ACTIVE;

    // UI Configuration
    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Primary color must be a valid hex color code")
    @Column(name = "primary_color", length = 7)
    private String primaryColor;

    // Database Infrastructure Configuration
    @Enumerated(EnumType.STRING)
    @Column(name = "db_type", nullable = false, length = 20)
    private DatabaseType dbType;

    @NotBlank(message = "JDBC URL is required")
    @Column(name = "jdbc_url", nullable = false, length = 500)
    private String jdbcUrl;

    @NotBlank(message = "Database username is required")
    @Column(name = "db_username", nullable = false, length = 100)
    private String dbUsername;

    @NotBlank(message = "Database password is required")
    @Column(name = "db_password", nullable = false, length = 255)
    private String dbPassword;

    @NotBlank(message = "Driver class name is required")
    @Column(name = "driver_class_name", nullable = false, length = 255)
    private String driverClassName;

    // Audit Fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public enum TenantStatus {
        ACTIVE,
        INACTIVE,
        TRIAL,
        SUSPENDED
    }

    public enum DatabaseType {
        POSTGRES,
        ORACLE
    }
}
