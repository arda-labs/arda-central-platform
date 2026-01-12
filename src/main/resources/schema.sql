-- Central Platform Database Schema
-- This schema stores tenant metadata and database connection information

CREATE TABLE IF NOT EXISTS tenants (
    id UUID PRIMARY KEY,
    tenant_key VARCHAR(50) NOT NULL UNIQUE,
    display_name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    -- UI Configuration
    logo_url VARCHAR(500),
    primary_color VARCHAR(7),

    -- Database Infrastructure Configuration
    db_type VARCHAR(20) NOT NULL,
    jdbc_url VARCHAR(500) NOT NULL,
    db_username VARCHAR(100) NOT NULL,
    db_password VARCHAR(255) NOT NULL,
    driver_class_name VARCHAR(255) NOT NULL,

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index for fast tenant lookup
CREATE INDEX IF NOT EXISTS idx_tenant_key ON tenants(tenant_key);

-- Index for status filtering
CREATE INDEX IF NOT EXISTS idx_tenant_status ON tenants(status);
