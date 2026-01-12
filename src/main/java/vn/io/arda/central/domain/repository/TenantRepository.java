package vn.io.arda.central.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.io.arda.central.domain.entity.Tenant;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    /**
     * Find tenant by unique tenant key
     *
     * @param tenantKey Unique tenant identifier
     * @return Optional containing the tenant if found
     */
    Optional<Tenant> findByTenantKey(String tenantKey);

    /**
     * Check if tenant exists by tenant key
     *
     * @param tenantKey Unique tenant identifier
     * @return true if tenant exists
     */
    boolean existsByTenantKey(String tenantKey);
}
