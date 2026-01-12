package vn.io.arda.central.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Public DTO for tenant UI information (safe for client-side)
 * Does NOT contain sensitive database configuration
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TenantPublicInfoDto(
    String tenantKey,
    String displayName,
    String status,
    String logoUrl,
    String primaryColor
) {
}
