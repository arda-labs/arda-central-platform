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
        String key, // Matches 'key' in Shell's TenantInfo
        String name, // Matches 'name' in Shell's TenantInfo
        String primaryColor,
        String logo, // Matches 'logo' in Shell's TenantInfo
        String dbType,
        String status) {
}
