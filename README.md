# Arda Central Platform

**Port:** 8000
**Database:** PostgreSQL (`arda_central`)
**Role:** Central management service for tenant metadata and database configuration

## Overview

This service acts as the "brain" of the Arda multi-tenant platform. It manages:
- Tenant metadata (name, branding, status)
- Database connection configurations for each tenant
- Public API for UI customization
- Internal API for service-to-service communication

## Prerequisites

### 1. GitHub Packages Authentication

Create environment variables for GitHub authentication:

```bash
# Windows
set GITHUB_USERNAME=your-github-username
set GITHUB_TOKEN=your-personal-access-token

# Linux/Mac
export GITHUB_USERNAME=your-github-username
export GITHUB_TOKEN=your-personal-access-token
```

To create a GitHub Personal Access Token:
1. Go to GitHub Settings → Developer settings → Personal access tokens
2. Generate new token (classic)
3. Select scope: `read:packages`
4. Copy the token

### 2. Database Setup

Create the central database:

```sql
-- Connect to PostgreSQL
psql -U postgres

-- Create central database
CREATE DATABASE arda_central;

-- The schema and seed data will be auto-created on first run
```

## Configuration

The service is pre-configured in application.yaml:

- **Port:** 8000
- **Database URL:** `jdbc:postgresql://localhost:5432/arda_central`
- **Username/Password:** `postgres/postgres` (change in production!)

## Running the Service

### Using Maven Wrapper

```bash
# Build the project
mvnw.cmd clean install

# Run the service
mvnw.cmd spring-boot:run
```

The service will:
1. Download `arda-shared-kernel` from GitHub Packages
2. Connect to PostgreSQL
3. Create schema if not exists
4. Insert seed data (3 sample tenants)
5. Start on port 8000

## API Endpoints

### Public API (Safe for Client-side)

**GET** `/api/v1/public/tenants/info/{tenantKey}`

Returns UI configuration (no sensitive data):
```json
{
  "tenantKey": "tenant_pg",
  "displayName": "PostgreSQL Demo Tenant",
  "status": "ACTIVE",
  "logoUrl": "https://...",
  "primaryColor": "#336791"
}
```

### Internal API (Service-to-Service Only)

**GET** `/api/v1/internal/tenants/config/{tenantKey}`

Returns complete database configuration (contains credentials):
```json
{
  "tenantKey": "tenant_pg",
  "displayName": "PostgreSQL Demo Tenant",
  "status": "ACTIVE",
  "dbType": "POSTGRES",
  "jdbcUrl": "jdbc:postgresql://localhost:5432/arda_tenant_pg",
  "dbUsername": "postgres",
  "dbPassword": "postgres",
  "driverClassName": "org.postgresql.Driver"
}
```

⚠️ **Security Warning:** Internal API should be protected by API Gateway or mTLS in production!

## Seed Data

Three sample tenants are created automatically:

| Tenant Key    | Database Type | Database Name         | Status |
|---------------|---------------|-----------------------|--------|
| tenant_pg     | PostgreSQL    | arda_tenant_pg        | ACTIVE |
| tenant_ora    | Oracle        | XE (arda_tenant_ora)  | ACTIVE |
| tenant_trial  | PostgreSQL    | arda_tenant_trial     | TRIAL  |

## Testing

```bash
# Test Public API
curl http://localhost:8000/api/v1/public/tenants/info/tenant_pg

# Test Internal API
curl http://localhost:8000/api/v1/internal/tenants/config/tenant_pg

# Test Error Handling (404)
curl http://localhost:8000/api/v1/public/tenants/info/nonexistent
```

## Architecture Notes

### Static DataSource Configuration

Unlike other services (CRM, IAM, BPM), this service uses a **static DataSource** pointing directly to the central database. It does NOT use `TenantRoutingDataSource` because:
- It manages tenant metadata, not tenant-specific business data
- All configuration is stored in one central database
- No multi-tenancy routing is needed

### Integration with Other Services

Other microservices (IAM, CRM, BPM) will:
1. Call `/api/v1/internal/tenants/config/{tenantKey}` on startup
2. Receive database connection details
3. Initialize `RoutingDataSource` with tenant-specific connections
4. Route queries to appropriate tenant databases

## Next Steps

After this service is running:
1. Create tenant databases (`arda_tenant_pg`, `arda_tenant_ora`)
2. Configure **arda-iam-service** (Port 8001) to consume this API
3. Set up APISIX Gateway to route external traffic
4. Implement API Key authentication for Internal API

## Project Structure

```
src/main/java/vn/io/arda/central/
├── controller/
│   ├── TenantPublicController.java      # Public API
│   └── TenantInternalController.java    # Internal API
├── service/
│   └── TenantService.java               # Business logic
├── domain/
│   ├── entity/Tenant.java               # JPA Entity
│   └── repository/TenantRepository.java # Data access
└── dto/
    ├── TenantPublicInfoDto.java         # Public DTO
    └── TenantDatabaseConfigDto.java     # Internal DTO

src/main/resources/
├── application.yaml                     # Configuration
├── schema.sql                           # Database schema
└── data.sql                             # Seed data
```

## Troubleshooting

### Cannot download arda-shared-kernel

```
Could not resolve vn.io.arda:arda-shared-kernel:0.0.1-SNAPSHOT
```

**Solution:**
- Verify GitHub token has `read:packages` permission
- Check environment variables are set
- Ensure `.mvn/settings.xml` exists

### Database connection error

```
Connection refused: localhost:5432
```

**Solution:**
- Start PostgreSQL: `docker-compose up -d postgres`
- Or install PostgreSQL locally
- Update `application.yaml` with correct credentials
