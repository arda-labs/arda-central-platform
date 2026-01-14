package vn.io.arda.central.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FederationRemoteDto {
    private String name;
    private String displayName;
    private String description;
    private String remoteEntryUrl;
    private String exposedModule;
    private String icon;
    private Boolean enabled;
    private Integer displayOrder;
}
