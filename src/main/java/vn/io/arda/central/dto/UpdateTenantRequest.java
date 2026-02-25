package vn.io.arda.central.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating tenant UI configuration.
 * Only allows updating display name, logo URL, and primary color.
 *
 * @since 0.0.2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTenantRequest {

    private String displayName;

    @Pattern(
            regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
            message = "Primary color must be a valid hex color code"
    )
    private String primaryColor;

    private String logoUrl;
}
