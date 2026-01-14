package vn.io.arda.central.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for tenant creation.
 *
 * @since 0.0.2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTenantResponse {

    private String tenantKey;
    private String status;
    private String message;

    public static CreateTenantResponse success(String tenantKey) {
        return new CreateTenantResponse(
                tenantKey,
                "SUCCESS",
                "Tenant created successfully. Keycloak realm provisioning in progress."
        );
    }

    public static CreateTenantResponse error(String tenantKey, String message) {
        return new CreateTenantResponse(tenantKey, "ERROR", message);
    }
}
