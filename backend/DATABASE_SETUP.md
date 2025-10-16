# Database Setup Guide

## Quick Start

### Option 1: Using Docker Compose (Recommended)

The easiest way to get started is using Docker Compose for the development database.

```bash
# Start PostgreSQL + PgAdmin
docker-compose -f docker-compose.dev.yml up -d

# Check if containers are running
docker ps

# View logs
docker-compose -f docker-compose.dev.yml logs -f postgres
```

**Database Access:**
- Host: localhost
- Port: 5432
- Database: financas_db
- Username: financas_user
- Password: financas_password

**PgAdmin Access:**
- URL: http://localhost:5050
- Email: admin@financas.com
- Password: admin

### Option 2: Local PostgreSQL Installation

If you have PostgreSQL installed locally:

```bash
# Connect to PostgreSQL
psql -U postgres

# Create database and user
CREATE DATABASE financas_db;
CREATE USER financas_user WITH PASSWORD 'financas_password';
GRANT ALL PRIVILEGES ON DATABASE financas_db TO financas_user;

# Connect to the new database
\c financas_db

# Grant schema privileges
GRANT ALL ON SCHEMA public TO financas_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO financas_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO financas_user;

# Exit
\q
```

## Running Migrations

Migrations run automatically when you start the Spring Boot application. Flyway is configured to:
- Execute migrations in order (V1, V2, V3, V4...)
- Create a `flyway_schema_history` table to track applied migrations
- Skip already-applied migrations

### Start the Application

```bash
cd backend

# Using Maven
mvn spring-boot:run

# Or build and run the JAR
mvn clean package
java -jar target/financial-assembly-backend-0.0.1-SNAPSHOT.jar
```

### Manual Migration (if needed)

```bash
# Check migration status
mvn flyway:info

# Apply pending migrations
mvn flyway:migrate

# Validate migrations
mvn flyway:validate

# View migration history
mvn flyway:info -Dflyway.url=jdbc:postgresql://localhost:5432/financas_db \
    -Dflyway.user=financas_user \
    -Dflyway.password=financas_password
```

## Verify Database Setup

### Check Tables

```bash
# Connect to database
psql -U financas_user -d financas_db -h localhost

# List all tables
\dt

# You should see:
#  - audit_logs
#  - categorias
#  - clientes
#  - flyway_schema_history
#  - permissions
#  - refresh_tokens
#  - role_permissions
#  - roles
#  - transacoes
#  - user_roles
#  - users
```

### Check Migration Status

```sql
-- View migration history
SELECT version, description, installed_on, success
FROM flyway_schema_history
ORDER BY installed_rank;

-- Expected output:
-- V1 | create initial schema
-- V2 | insert initial data
-- V3 | create users and authentication
-- V4 | insert roles permissions default user
```

### Check Sample Data

```sql
-- Check categories
SELECT COUNT(*) FROM categorias;
-- Expected: 29 categories

-- Check sample clients
SELECT tipo_cliente, COUNT(*) FROM clientes GROUP BY tipo_cliente;
-- Expected: 3 PF, 2 PJ

-- Check sample transactions
SELECT tipo, COUNT(*) FROM transacoes GROUP BY tipo;
-- Expected: 3 RECEITA, 6 DESPESA

-- Check users
SELECT name, email FROM users;
-- Expected: 4 users (admin, manager, user, viewer)

-- Check roles
SELECT name FROM roles;
-- Expected: ROLE_ADMIN, ROLE_USER, ROLE_MANAGER, ROLE_VIEWER
```

## Default Credentials

### Admin Account
- **Email:** admin@financialassembly.com
- **Password:** admin123
- **Role:** ROLE_ADMIN

### Demo Accounts
| Email | Password | Role |
|-------|----------|------|
| manager@financialassembly.com | password123 | ROLE_MANAGER |
| user@financialassembly.com | password123 | ROLE_USER |
| viewer@financialassembly.com | password123 | ROLE_VIEWER |

**⚠️ IMPORTANT:** Change these passwords in production!

## Database Schema Overview

### Migration Files

1. **V1__create_initial_schema.sql** (6.6 KB)
   - Creates business domain tables: categorias, clientes, transacoes
   - Adds indexes and constraints
   - Sets up single-table inheritance for clients (PF/PJ)

2. **V2__insert_initial_data.sql** (8.9 KB)
   - Inserts 29 default categories (income/expense)
   - Adds subcategories for common categories
   - Creates sample clients (3 PF, 2 PJ)
   - Inserts sample transactions

3. **V3__create_users_and_authentication.sql** (7.0 KB)
   - Creates authentication tables: users, roles, permissions
   - Sets up RBAC (Role-Based Access Control)
   - Creates refresh_tokens table for JWT
   - Creates audit_logs table for compliance

4. **V4__insert_roles_permissions_default_user.sql** (8.1 KB)
   - Creates 4 default roles with descriptions
   - Defines 32 permissions across resources
   - Maps permissions to roles
   - Creates default admin and demo users

### Total Tables: 11

**Authentication (6 tables):**
- users
- roles
- user_roles
- permissions
- role_permissions
- refresh_tokens

**Business Domain (3 tables):**
- categorias
- clientes
- transacoes

**Audit (1 table):**
- audit_logs

**Flyway (1 table):**
- flyway_schema_history

## Common Tasks

### Reset Database (Development Only)

```bash
# Drop and recreate database
docker-compose -f docker-compose.dev.yml down -v
docker-compose -f docker-compose.dev.yml up -d

# Wait a few seconds for PostgreSQL to start
sleep 5

# Restart application (migrations will run automatically)
mvn spring-boot:run
```

### Backup Database

```bash
# Create backup
pg_dump -U financas_user -h localhost financas_db > backup_$(date +%Y%m%d_%H%M%S).sql

# Restore from backup
psql -U financas_user -h localhost -d financas_db < backup_20241016_182200.sql
```

### Connect to Database

```bash
# Using psql
psql -U financas_user -d financas_db -h localhost

# Using PgAdmin (Web Interface)
# Open http://localhost:5050
# Add new server with connection details above
```

### View Logs

```bash
# PostgreSQL logs (Docker)
docker logs financas_assembly_postgres -f

# Application logs (shows migration execution)
tail -f logs/application.log
```

## Troubleshooting

### Issue: Cannot connect to database

**Solution:**
```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Restart PostgreSQL
docker-compose -f docker-compose.dev.yml restart postgres

# Check PostgreSQL logs
docker logs financas_assembly_postgres
```

### Issue: Flyway migration failed

**Solution:**
```bash
# Check Flyway history
psql -U financas_user -d financas_db -h localhost -c "SELECT * FROM flyway_schema_history;"

# If a migration is marked as failed, repair it
mvn flyway:repair

# Or drop and recreate (DEVELOPMENT ONLY)
docker-compose -f docker-compose.dev.yml down -v
docker-compose -f docker-compose.dev.yml up -d
```

### Issue: Permission denied errors

**Solution:**
```sql
-- Connect as postgres user
psql -U postgres

-- Grant all privileges
GRANT ALL PRIVILEGES ON DATABASE financas_db TO financas_user;
\c financas_db
GRANT ALL ON SCHEMA public TO financas_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO financas_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO financas_user;
```

### Issue: Port 5432 already in use

**Solution:**
```bash
# Find process using port 5432
sudo lsof -i :5432

# Stop existing PostgreSQL
sudo systemctl stop postgresql

# Or change port in docker-compose.dev.yml
# ports:
#   - "5433:5432"  # Use 5433 instead
```

## Environment-Specific Configuration

### Development
File: `application-dev.properties`
- Uses local PostgreSQL
- Show SQL queries in logs
- Flyway auto-migration enabled

### Production
File: `application-prod.properties`
- Uses environment variables for credentials
- SQL logging disabled
- Flyway validate mode (manual migration)

### Test
File: `application-test.properties`
- Uses H2 in-memory database for tests
- Schema created from scratch each test

## Security Best Practices

1. **Change default passwords immediately**
   - Admin password should be complex
   - Demo accounts should be disabled/deleted in production

2. **Use environment variables**
   ```bash
   export DB_URL=jdbc:postgresql://localhost:5432/financas_db
   export DB_USERNAME=financas_user
   export DB_PASSWORD=your_secure_password
   ```

3. **Enable SSL in production**
   ```properties
   spring.datasource.url=jdbc:postgresql://host:5432/db?ssl=true&sslmode=require
   ```

4. **Restrict database user privileges**
   - Don't use superuser accounts
   - Grant only necessary permissions

5. **Regular backups**
   - Daily automated backups
   - Test restore procedures

## Next Steps

After setting up the database:

1. ✅ Verify all tables are created
2. ✅ Check sample data is inserted
3. ✅ Test login with admin account
4. 🔨 Implement JPA entities (next task)
5. 🔨 Create repositories
6. 🔨 Implement services
7. 🔨 Build REST controllers

## Resources

- **Flyway Documentation:** https://flywaydb.org/documentation/
- **PostgreSQL Documentation:** https://www.postgresql.org/docs/
- **Spring Boot & Flyway:** https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway

---

**Status:** ✅ Database schema complete and ready for use
**Last Updated:** 2025-10-16
**Schema Version:** V4
