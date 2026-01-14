package vn.io.arda.central.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new tenant.
 *
 * @since 0.0.2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTenantRequest {

    @NotBlank(message = "Tenant key is required")
    @Pattern(
            regexp = "^[a-z0-9_-]+$",
            message = "Tenant key must contain only lowercase letters, numbers, hyphens, and underscores"
    )
    private String tenantKey;

    @NotBlank(message = "Display name is required")
    private String displayName;

    private String logoUrl;

    @Pattern(
            regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
            message = "Primary color must be a valid hex color code"
    )
    private String primaryColor;

    @NotBlank(message = "Database type is required")
    @Pattern(regexp = "^(POSTGRES|ORACLE)$", message = "Database type must be POSTGRES or ORACLE")
    private String dbType;

    @NotBlank(message = "JDBC URL is required")
    private String jdbcUrl;

    @NotBlank(message = "Database username is required")
    private String dbUsername;

    @NotBlank(message = "Database password is required")
    private String dbPassword;

    @NotBlank(message = "Driver class name is required")
    private String driverClassName;
}
