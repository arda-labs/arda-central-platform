-- Federation Module Configuration Table
-- Stores dynamic federation remote modules configuration

CREATE TABLE IF NOT EXISTS federation_remotes (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),

    -- Module Configuration
    remote_entry_url VARCHAR(500) NOT NULL,
    exposed_module VARCHAR(100) DEFAULT './Component',
    icon VARCHAR(50),

    -- Status and Ordering
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INT NOT NULL DEFAULT 0,

    -- Tenant-specific overrides (optional)
    tenant_key VARCHAR(50),

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key to tenants (optional, for tenant-specific configs)
    FOREIGN KEY (tenant_key) REFERENCES tenants(tenant_key) ON DELETE CASCADE
);

-- Index for fast lookup by name
CREATE INDEX IF NOT EXISTS idx_federation_name ON federation_remotes(name);

-- Index for enabled modules
CREATE INDEX IF NOT EXISTS idx_federation_enabled ON federation_remotes(enabled);

-- Index for tenant-specific configs
CREATE INDEX IF NOT EXISTS idx_federation_tenant ON federation_remotes(tenant_key);

-- Index for ordering
CREATE INDEX IF NOT EXISTS idx_federation_order ON federation_remotes(display_order);
