package vn.io.arda.central.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Internal DTO for tenant database configuration
 * Contains sensitive information - should only be used by internal services
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TenantDatabaseConfigDto(
    String tenantKey,
    String displayName,
    String status,
    String dbType,
    String jdbcUrl,
    String dbUsername,
    String dbPassword,
    String driverClassName
) {
}
