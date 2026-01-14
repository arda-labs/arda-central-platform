package vn.io.arda.central.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.io.arda.central.domain.entity.FederationRemote;
import vn.io.arda.central.dto.FederationRemoteDto;
import vn.io.arda.central.repository.FederationRemoteRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FederationRemoteService {

    private final FederationRemoteRepository federationRemoteRepository;

    /**
     * Get all enabled federation remotes
     */
    @Transactional(readOnly = true)
    public List<FederationRemoteDto> getEnabledRemotes() {
        log.info("Fetching all enabled federation remotes");
        List<FederationRemote> remotes = federationRemoteRepository.findByEnabledTrueOrderByDisplayOrderAsc();
        return remotes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get enabled remotes for a specific tenant
     * Returns global remotes + tenant-specific remotes
     */
    @Transactional(readOnly = true)
    public List<FederationRemoteDto> getRemotesForTenant(String tenantKey) {
        log.info("Fetching federation remotes for tenant: {}", tenantKey);

        // Get global remotes (tenant_key is null) + tenant-specific remotes
        List<FederationRemote> remotes = federationRemoteRepository
                .findByTenantKeyOrTenantKeyIsNullOrderByDisplayOrderAsc(tenantKey);

        return remotes.stream()
                .filter(FederationRemote::getEnabled)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get global remotes only (tenant-agnostic)
     */
    @Transactional(readOnly = true)
    public List<FederationRemoteDto> getGlobalRemotes() {
        log.info("Fetching global federation remotes");
        List<FederationRemote> remotes = federationRemoteRepository
                .findByTenantKeyIsNullAndEnabledTrueOrderByDisplayOrderAsc();
        return remotes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert entity to DTO
     */
    private FederationRemoteDto convertToDto(FederationRemote entity) {
        return FederationRemoteDto.builder()
                .name(entity.getName())
                .displayName(entity.getDisplayName())
                .description(entity.getDescription())
                .remoteEntryUrl(entity.getRemoteEntryUrl())
                .exposedModule(entity.getExposedModule())
                .icon(entity.getIcon())
                .enabled(entity.getEnabled())
                .displayOrder(entity.getDisplayOrder())
                .build();
    }
}
