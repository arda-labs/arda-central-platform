package vn.io.arda.central.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Public DTO for tenant UI information (safe for client-side)
 * Does NOT contain sensitive database configuration
 *
 * Maps to TenantInfo interface in Angular frontend
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TenantPublicInfoDto(
    String tenantCode,    // Changed from tenantKey to match frontend interface
    String tenantName,    // Changed from displayName to match frontend interface
    String primaryColor,
    String logoUrl,
    String dbType,        // Added to match frontend interface (POSTGRESQL/ORACLE)
    String status         // ACTIVE/INACTIVE
) {
}
