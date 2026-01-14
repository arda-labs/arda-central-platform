package vn.io.arda.central.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.io.arda.central.dto.FederationRemoteDto;
import vn.io.arda.central.service.FederationRemoteService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Public API for federation remote modules configuration
 * Used by the Shell application to load remote micro-frontends dynamically
 */
@RestController
@RequestMapping("/api/v1/federation")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Allow CORS for local development
public class FederationController {

    private final FederationRemoteService federationRemoteService;

    /**
     * Get all enabled federation remotes (global)
     * Returns list of remote modules available for loading
     *
     * @return List of federation remote configurations
     */
    @GetMapping("/remotes")
    public ResponseEntity<List<FederationRemoteDto>> getEnabledRemotes() {
        log.info("API: Fetching all enabled federation remotes");
        List<FederationRemoteDto> remotes = federationRemoteService.getEnabledRemotes();
        return ResponseEntity.ok(remotes);
    }

    /**
     * Get federation manifest in Native Federation format
     * Returns a map of module name -> remoteEntry.json URL
     *
     * @return Map suitable for Native Federation manifest
     */
    @GetMapping("/manifest")
    public ResponseEntity<Map<String, String>> getFederationManifest() {
        log.info("API: Fetching federation manifest");
        List<FederationRemoteDto> remotes = federationRemoteService.getEnabledRemotes();

        Map<String, String> manifest = new HashMap<>();
        for (FederationRemoteDto remote : remotes) {
            manifest.put(remote.getName(), remote.getRemoteEntryUrl());
        }

        return ResponseEntity.ok(manifest);
    }

    /**
     * Get federation remotes for a specific tenant
     * Includes global remotes + tenant-specific overrides
     *
     * @param tenantKey Tenant identifier
     * @return List of federation remote configurations for the tenant
     */
    @GetMapping("/remotes/tenant/{tenantKey}")
    public ResponseEntity<List<FederationRemoteDto>> getRemotesForTenant(@PathVariable String tenantKey) {
        log.info("API: Fetching federation remotes for tenant: {}", tenantKey);
        List<FederationRemoteDto> remotes = federationRemoteService.getRemotesForTenant(tenantKey);
        return ResponseEntity.ok(remotes);
    }

    /**
     * Get federation manifest for a specific tenant
     *
     * @param tenantKey Tenant identifier
     * @return Map suitable for Native Federation manifest
     */
    @GetMapping("/manifest/tenant/{tenantKey}")
    public ResponseEntity<Map<String, String>> getFederationManifestForTenant(@PathVariable String tenantKey) {
        log.info("API: Fetching federation manifest for tenant: {}", tenantKey);
        List<FederationRemoteDto> remotes = federationRemoteService.getRemotesForTenant(tenantKey);

        Map<String, String> manifest = new HashMap<>();
        for (FederationRemoteDto remote : remotes) {
            manifest.put(remote.getName(), remote.getRemoteEntryUrl());
        }

        return ResponseEntity.ok(manifest);
    }
}
