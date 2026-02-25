package vn.io.arda.central.dto;

import lombok.*;

import java.util.UUID;

/**
 * Request DTO for updating a tenant's menu activation config.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantMenuRequest {

    private UUID menuId;
    private Boolean enabled;
    private Integer sortOrderOverride;
}
