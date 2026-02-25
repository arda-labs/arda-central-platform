package vn.io.arda.central.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating tenant status.
 *
 * @since 0.0.2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequest {

    @NotBlank(message = "Status is required")
    @Pattern(
            regexp = "^(ACTIVE|INACTIVE|TRIAL|SUSPENDED)$",
            message = "Status must be one of: ACTIVE, INACTIVE, TRIAL, SUSPENDED"
    )
    private String status;
}
