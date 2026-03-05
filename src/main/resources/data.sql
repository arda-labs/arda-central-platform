-- Seed Data for Central Platform
-- Sample tenants for demo purposes

-- Tenant 1: PostgreSQL Tenant (Demo Company using Postgres)
INSERT INTO tenants (id, tenant_key, display_name, status, logo_url, primary_color, db_type, jdbc_url, db_username, db_password, driver_class_name, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'tenant_pg',
    'PostgreSQL Demo Tenant',
    'ACTIVE',
    'https://www.postgresql.org/media/img/about/press/elephant.png',
    '#336791',
    'POSTGRES',
    'jdbc:postgresql://localhost:5432/arda_tenant_pg',
    'postgres',
    'postgres',
    'org.postgresql.Driver',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (tenant_key) DO NOTHING;

-- Tenant 2: Secondary PG Tenant
INSERT INTO tenants (id, tenant_key, display_name, status, logo_url, primary_color, db_type, jdbc_url, db_username, db_password, driver_class_name, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'tenant_base',
    'Base Demo Tenant',
    'ACTIVE',
    NULL,
    '#10B981',
    'POSTGRES',
    'jdbc:postgresql://localhost:5432/arda_tenant_base',
    'postgres',
    'postgres',
    'org.postgresql.Driver',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (tenant_key) DO NOTHING;

-- Tenant 3: Trial Tenant (Inactive tenant for testing)
INSERT INTO tenants (id, tenant_key, display_name, status, logo_url, primary_color, db_type, jdbc_url, db_username, db_password, driver_class_name, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'tenant_trial',
    'Trial Customer',
    'TRIAL',
    NULL,
    '#6366F1',
    'POSTGRES',
    'jdbc:postgresql://localhost:5432/arda_tenant_trial',
    'postgres',
    'postgres',
    'org.postgresql.Driver',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (tenant_key) DO NOTHING;

-- Tenant 4: Vinamilk (Real-world example)
INSERT INTO tenants (id, tenant_key, display_name, status, logo_url, primary_color, db_type, jdbc_url, db_username, db_password, driver_class_name, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'vinamilk',
    'Vinamilk',
    'ACTIVE',
    'https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/Vinamilk_logo.svg/200px-Vinamilk_logo.svg.png',
    '#0066CC',
    'POSTGRES',
    'jdbc:postgresql://localhost:5432/arda_vinamilk',
    'postgres',
    'postgres',
    'org.postgresql.Driver',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (tenant_key) DO NOTHING;

-- Tenant 5: FPT Corporation (Real-world example)
INSERT INTO tenants (id, tenant_key, display_name, status, logo_url, primary_color, db_type, jdbc_url, db_username, db_password, driver_class_name, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'fpt',
    'FPT Corporation',
    'ACTIVE',
    'https://upload.wikimedia.org/wikipedia/commons/thumb/1/1f/FPT_logo_2010.svg/200px-FPT_logo_2010.svg.png',
    '#FF6600',
    'POSTGRES',
    'jdbc:postgresql://localhost:5432/arda_fpt',
    'postgres',
    'postgres',
    'org.postgresql.Driver',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (tenant_key) DO NOTHING;

-- Tenant 6: Viettel Group
INSERT INTO tenants (id, tenant_key, display_name, status, logo_url, primary_color, db_type, jdbc_url, db_username, db_password, driver_class_name, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'viettel',
    'Viettel Group',
    'ACTIVE',
    'https://upload.wikimedia.org/wikipedia/commons/thumb/d/d0/Viettel_logo.svg/200px-Viettel_logo.svg.png',
    '#EC1C24',
    'POSTGRES',
    'jdbc:postgresql://localhost:5432/arda_viettel',
    'postgres',
    'postgres',
    'org.postgresql.Driver',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (tenant_key) DO NOTHING;
