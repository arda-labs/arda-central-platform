package vn.io.arda.central.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO for Menu entity — matches the ApiMenuItem interface on the frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDto {

    private UUID id;
    private String label;
    private String path;
    private String icon;
    private String iconColor;
    private Integer sortOrder;
    private List<MenuDto> children;
    private Instant createdAt;
    private Instant updatedAt;
}
