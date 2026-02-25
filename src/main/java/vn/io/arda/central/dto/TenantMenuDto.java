package vn.io.arda.central.dto;

import lombok.*;

import java.util.UUID;

/**
 * DTO for tenant-specific menu configuration entry.
 * Used in the admin UI to show which menus a tenant has enabled.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantMenuDto {

    private UUID id;
    private String tenantKey;
    private MenuDto menu;
    private Boolean enabled;
    private Integer sortOrderOverride;
}
