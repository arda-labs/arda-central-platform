package vn.io.arda.central.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuReorderRequest {
  private UUID id;
  private UUID parentId;
  private Integer sortOrder;
}
