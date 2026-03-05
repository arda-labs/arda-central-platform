package vn.io.arda.central.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * Detailed DTO for tenant information including database configuration.
 * Used for SUPER_ADMIN views only.
 * Contains sensitive information like JDBC URL and database username.
 *
 * @since 0.0.2
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TenantDetailDto(
        String key,
        String name,
        String primaryColor,
        String logo,
        String status,
        String jdbcUrl,
        String dbUsername,
        Instant createdAt,
        Instant updatedAt
) {
}
