package vn.io.arda.central.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.io.arda.central.domain.entity.FederationRemote;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FederationRemoteRepository extends JpaRepository<FederationRemote, UUID> {

    /**
     * Find all enabled federation remotes ordered by display_order
     */
    List<FederationRemote> findByEnabledTrueOrderByDisplayOrderAsc();

    /**
     * Find all remotes for a specific tenant (or global if tenantKey is null)
     */
    List<FederationRemote> findByTenantKeyOrTenantKeyIsNullOrderByDisplayOrderAsc(String tenantKey);

    /**
     * Find a remote by name
     */
    Optional<FederationRemote> findByName(String name);

    /**
     * Find all enabled remotes for a specific tenant
     */
    List<FederationRemote> findByEnabledTrueAndTenantKeyOrderByDisplayOrderAsc(String tenantKey);

    /**
     * Find all global (tenant-agnostic) remotes
     */
    List<FederationRemote> findByTenantKeyIsNullAndEnabledTrueOrderByDisplayOrderAsc();
}
