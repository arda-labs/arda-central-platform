-- Seed Data for Central Platform
-- Two sample tenants: one using PostgreSQL, one using Oracle

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
);

-- Tenant 2: Oracle Tenant (Demo Company using Oracle)
INSERT INTO tenants (id, tenant_key, display_name, status, logo_url, primary_color, db_type, jdbc_url, db_username, db_password, driver_class_name, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'tenant_ora',
    'Oracle Demo Tenant',
    'ACTIVE',
    'https://www.oracle.com/a/tech/img/oracle-database-logo.png',
    '#F80000',
    'ORACLE',
    'jdbc:oracle:thin:@localhost:1521:XE',
    'arda_tenant_ora',
    'oracle123',
    'oracle.jdbc.OracleDriver',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

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
);
