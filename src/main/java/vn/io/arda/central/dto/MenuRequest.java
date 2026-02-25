package vn.io.arda.central.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

/**
 * Request DTO for creating or updating a master menu item.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuRequest {

    @NotBlank(message = "Label is required")
    @Size(max = 100)
    private String label;

    @Size(max = 255)
    private String path;

    @Size(max = 100)
    private String icon;

    @Size(max = 50)
    private String iconColor;

    private Integer sortOrder;

    /**
     * Parent menu ID. Null for top-level items.
     */
    private UUID parentId;
}
