# Financial Assembly - Database Schema Documentation

## Overview
This document describes the complete database schema for the Financial Assembly application, including all tables, relationships, indexes, and constraints.

## Database Configuration
- **Database:** PostgreSQL 16
- **Migration Tool:** Flyway
- **Character Set:** UTF-8
- **Timezone:** America/Sao_Paulo

## Schema Versions

### V1 - Initial Schema (Tables for Business Domain)
- categorias
- clientes
- transacoes

### V2 - Initial Data (Seed Data)
- Default categories (income/expense)
- Sample clients (PF and PJ)
- Sample transactions

### V3 - Authentication Schema
- users
- roles
- user_roles
- permissions
- role_permissions
- refresh_tokens
- audit_logs

### V4 - Default Users and Permissions
- Default roles (ADMIN, USER, MANAGER, VIEWER)
- Permission definitions
- Role-permission mappings
- Default admin and demo users

## Table Definitions

### 1. Users & Authentication

#### **users**
Stores system user accounts with authentication credentials.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique user identifier |
| name | VARCHAR(100) | NOT NULL | User's full name |
| email | VARCHAR(100) | NOT NULL, UNIQUE | Email address (login) |
| password | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| active | BOOLEAN | NOT NULL, DEFAULT true | Account active status |
| email_verified | BOOLEAN | NOT NULL, DEFAULT false | Email verification status |
| email_verification_token | VARCHAR(255) | | Token for email verification |
| email_verification_expiry | TIMESTAMP | | Token expiry time |
| password_reset_token | VARCHAR(255) | | Token for password reset |
| password_reset_expiry | TIMESTAMP | | Reset token expiry |
| last_login | TIMESTAMP | | Last successful login |
| login_attempts | INTEGER | DEFAULT 0 | Failed login counter |
| locked_until | TIMESTAMP | | Account lock expiry |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | | Last update timestamp |
| created_by | VARCHAR(255) | | User who created record |
| updated_by | VARCHAR(255) | | User who last updated |
| version | BIGINT | DEFAULT 0 | Optimistic locking version |

**Indexes:**
- idx_user_email (email)
- idx_user_active (active)
- idx_user_email_verification_token (email_verification_token)
- idx_user_password_reset_token (password_reset_token)

**Default Users:**
| Email | Password | Role | Description |
|-------|----------|------|-------------|
| admin@financialassembly.com | admin123 | ADMIN | System administrator |
| manager@financialassembly.com | password123 | MANAGER | Manager user |
| user@financialassembly.com | password123 | USER | Regular user |
| viewer@financialassembly.com | password123 | VIEWER | Read-only user |

**⚠️ IMPORTANT:** Change the admin password immediately after first deployment!

#### **roles**
Defines user roles for role-based access control (RBAC).

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique role identifier |
| name | VARCHAR(50) | NOT NULL, UNIQUE | Role name (e.g., ROLE_ADMIN) |
| description | VARCHAR(255) | | Role description |
| active | BOOLEAN | NOT NULL, DEFAULT true | Role active status |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | | Last update timestamp |
| version | BIGINT | DEFAULT 0 | Optimistic locking version |

**Default Roles:**
- **ROLE_ADMIN**: Full system access
- **ROLE_USER**: Standard user access
- **ROLE_MANAGER**: Elevated privileges
- **ROLE_VIEWER**: Read-only access

#### **user_roles**
Maps users to their assigned roles (many-to-many).

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| user_id | BIGINT | NOT NULL, FK → users(id) | User reference |
| role_id | BIGINT | NOT NULL, FK → roles(id) | Role reference |
| created_at | TIMESTAMP | NOT NULL | Assignment timestamp |

**Primary Key:** (user_id, role_id)

**Indexes:**
- idx_user_roles_user (user_id)
- idx_user_roles_role (role_id)

#### **permissions**
Defines granular permissions for resources and actions.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique permission identifier |
| name | VARCHAR(100) | NOT NULL, UNIQUE | Permission name |
| resource | VARCHAR(50) | NOT NULL | Resource type |
| action | VARCHAR(50) | NOT NULL | Action type |
| description | VARCHAR(255) | | Permission description |
| active | BOOLEAN | NOT NULL, DEFAULT true | Permission active status |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | | Last update timestamp |
| version | BIGINT | DEFAULT 0 | Optimistic locking version |

**Unique Constraint:** (resource, action)

**Permission Categories:**
- **CLIENTE**: CREATE, READ, UPDATE, DELETE, LIST
- **TRANSACAO**: CREATE, READ, UPDATE, DELETE, LIST, APPROVE, CANCEL
- **CATEGORIA**: CREATE, READ, UPDATE, DELETE, LIST
- **USER**: CREATE, READ, UPDATE, DELETE, LIST
- **REPORT**: VIEW, EXPORT, CREATE
- **SYSTEM**: SETTINGS, AUDIT, BACKUP

#### **role_permissions**
Maps roles to their assigned permissions (many-to-many).

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| role_id | BIGINT | NOT NULL, FK → roles(id) | Role reference |
| permission_id | BIGINT | NOT NULL, FK → permissions(id) | Permission reference |
| created_at | TIMESTAMP | NOT NULL | Assignment timestamp |

**Primary Key:** (role_id, permission_id)

**Indexes:**
- idx_role_permissions_role (role_id)
- idx_role_permissions_permission (permission_id)

#### **refresh_tokens**
Stores JWT refresh tokens for session management.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique token identifier |
| user_id | BIGINT | NOT NULL, FK → users(id) | User reference |
| token | VARCHAR(255) | NOT NULL, UNIQUE | Refresh token value |
| expiry_date | TIMESTAMP | NOT NULL | Token expiry date |
| revoked | BOOLEAN | NOT NULL, DEFAULT false | Revocation status |
| revoked_at | TIMESTAMP | | Revocation timestamp |
| device_info | VARCHAR(255) | | Device information |
| ip_address | VARCHAR(45) | | Client IP address |
| created_at | TIMESTAMP | NOT NULL | Token creation timestamp |

**Indexes:**
- idx_refresh_token_user (user_id)
- idx_refresh_token_token (token)
- idx_refresh_token_expiry (expiry_date)
- idx_refresh_token_revoked (revoked)

#### **audit_logs**
System audit trail for compliance and security.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique log identifier |
| user_id | BIGINT | FK → users(id) | User who performed action |
| action | VARCHAR(100) | NOT NULL | Action performed |
| entity_type | VARCHAR(100) | | Entity type affected |
| entity_id | BIGINT | | Entity ID affected |
| old_value | TEXT | | Previous value (JSON) |
| new_value | TEXT | | New value (JSON) |
| ip_address | VARCHAR(45) | | Client IP address |
| user_agent | VARCHAR(255) | | Browser user agent |
| created_at | TIMESTAMP | NOT NULL | Action timestamp |

**Indexes:**
- idx_audit_log_user (user_id)
- idx_audit_log_action (action)
- idx_audit_log_entity (entity_type, entity_id)
- idx_audit_log_created_at (created_at DESC)

### 2. Business Domain

#### **categorias**
Transaction categories for income and expense classification.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique category identifier |
| nome | VARCHAR(100) | NOT NULL, UNIQUE | Category name |
| descricao | VARCHAR(500) | | Category description |
| tipo | VARCHAR(20) | NOT NULL, CHECK | Category type (RECEITA/DESPESA) |
| cor | VARCHAR(7) | | Color code (hex) |
| icone | VARCHAR(50) | | Material icon name |
| ativa | BOOLEAN | NOT NULL, DEFAULT true | Active status |
| categoria_pai_id | BIGINT | FK → categorias(id) | Parent category (for subcategories) |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | | Last update timestamp |
| created_by | VARCHAR(255) | | User who created record |
| updated_by | VARCHAR(255) | | User who last updated |
| version | BIGINT | DEFAULT 0 | Optimistic locking version |

**Indexes:**
- idx_categoria_pai (categoria_pai_id)
- idx_categoria_tipo (tipo)
- idx_categoria_ativa (ativa)

**Check Constraints:**
- tipo IN ('RECEITA', 'DESPESA')

**Hierarchical Structure:**
- Supports parent-child relationships for subcategories
- Self-referencing foreign key to categoria_pai_id

**Default Categories:**
- 10 income categories (Salário, Freelance, Investimentos, etc.)
- 17 expense categories (Alimentação, Transporte, Moradia, etc.)
- Subcategories for common categories (e.g., Supermercado, Restaurantes under Alimentação)

#### **clientes**
Client information using single table inheritance for PF (Pessoa Física) and PJ (Pessoa Jurídica).

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique client identifier |
| tipo_cliente | VARCHAR(10) | NOT NULL, CHECK | Client type (PF/PJ) |
| nome | VARCHAR(200) | NOT NULL | Client name |
| email | VARCHAR(100) | | Email address |
| telefone | VARCHAR(20) | | Phone number |
| endereco | VARCHAR(200) | | Street address |
| cidade | VARCHAR(100) | | City |
| estado | VARCHAR(2) | | State code |
| cep | VARCHAR(10) | | Postal code |
| ativo | BOOLEAN | NOT NULL, DEFAULT true | Active status |
| observacoes | VARCHAR(1000) | | Notes |

**Pessoa Física (PF) Fields:**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| cpf | VARCHAR(11) | UNIQUE | Brazilian tax ID (individuals) |
| rg | VARCHAR(9) | | ID document number |
| data_nascimento | DATE | | Birth date |
| profissao | VARCHAR(50) | | Profession |
| estado_civil | VARCHAR(20) | | Marital status |

**Pessoa Jurídica (PJ) Fields:**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| cnpj | VARCHAR(14) | UNIQUE | Brazilian tax ID (companies) |
| razao_social | VARCHAR(200) | | Legal company name |
| nome_fantasia | VARCHAR(200) | | Trade name |
| inscricao_estadual | VARCHAR(20) | | State tax registration |
| inscricao_municipal | VARCHAR(20) | | City tax registration |
| data_fundacao | DATE | | Foundation date |
| setor | VARCHAR(100) | | Business sector |
| porte | VARCHAR(20) | | Company size |
| responsavel_nome | VARCHAR(100) | | Contact person name |
| responsavel_email | VARCHAR(100) | | Contact person email |
| responsavel_telefone | VARCHAR(20) | | Contact person phone |

**Audit Fields:**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | | Last update timestamp |
| created_by | VARCHAR(255) | | User who created record |
| updated_by | VARCHAR(255) | | User who last updated |
| version | BIGINT | DEFAULT 0 | Optimistic locking version |

**Indexes:**
- idx_cliente_tipo (tipo_cliente)
- idx_cliente_ativo (ativo)
- idx_cliente_cpf (cpf) - partial, WHERE cpf IS NOT NULL
- idx_cliente_cnpj (cnpj) - partial, WHERE cnpj IS NOT NULL
- idx_cliente_nome (nome)
- idx_cliente_email (email)

**Check Constraints:**
- tipo_cliente IN ('PF', 'PJ')
- chk_pf_cpf: If tipo_cliente = 'PF', cpf must not be null
- chk_pj_cnpj: If tipo_cliente = 'PJ', cnpj must not be null

**Single Table Inheritance Strategy:**
- Uses discriminator column `tipo_cliente`
- PF-specific fields null for PJ clients
- PJ-specific fields null for PF clients

#### **transacoes**
Financial transactions with comprehensive tracking.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique transaction identifier |
| tipo | VARCHAR(20) | NOT NULL, CHECK | Transaction type (RECEITA/DESPESA) |
| valor | NUMERIC(12, 2) | NOT NULL, CHECK > 0 | Transaction amount |
| data_transacao | DATE | NOT NULL | Transaction date |
| descricao | VARCHAR(200) | NOT NULL | Description |
| observacoes | VARCHAR(1000) | | Additional notes |
| categoria_id | BIGINT | NOT NULL, FK → categorias(id) | Category reference |
| cliente_id | BIGINT | FK → clientes(id) | Client reference (optional) |
| metodo_pagamento | VARCHAR(30) | NOT NULL, CHECK | Payment method |
| status | VARCHAR(20) | NOT NULL, CHECK | Transaction status |
| efetivada | BOOLEAN | NOT NULL, DEFAULT false | Completion status |
| data_efetivacao | TIMESTAMP | | Completion timestamp |
| numero_documento | VARCHAR(100) | | Document number |
| recorrente | BOOLEAN | NOT NULL, DEFAULT false | Recurring flag |
| frequencia_recorrencia | VARCHAR(20) | CHECK | Recurrence frequency |
| data_fim_recorrencia | DATE | | Recurrence end date |
| transacao_pai_id | BIGINT | FK → transacoes(id) | Parent transaction reference |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | | Last update timestamp |
| created_by | VARCHAR(255) | | User who created record |
| updated_by | VARCHAR(255) | | User who last updated |
| version | BIGINT | DEFAULT 0 | Optimistic locking version |

**Indexes:**
- idx_transacao_data (data_transacao DESC)
- idx_transacao_tipo (tipo)
- idx_transacao_cliente (cliente_id)
- idx_transacao_categoria (categoria_id)
- idx_transacao_status (status)
- idx_transacao_efetivada (efetivada)
- idx_transacao_recorrente (recorrente)
- idx_transacao_pai (transacao_pai_id)
- idx_transacao_data_tipo (data_transacao DESC, tipo) - composite
- idx_transacao_cliente_data (cliente_id, data_transacao DESC) - composite
- idx_transacao_categoria_data (categoria_id, data_transacao DESC) - composite

**Check Constraints:**
- tipo IN ('RECEITA', 'DESPESA')
- valor > 0
- metodo_pagamento IN ('DINHEIRO', 'CARTAO_CREDITO', 'CARTAO_DEBITO', 'TRANSFERENCIA', 'PIX', 'BOLETO', 'CHEQUE', 'OUTRO')
- status IN ('PENDENTE', 'CONFIRMADA', 'CANCELADA', 'ESTORNADA')
- frequencia_recorrencia IN ('DIARIA', 'SEMANAL', 'QUINZENAL', 'MENSAL', 'BIMESTRAL', 'TRIMESTRAL', 'SEMESTRAL', 'ANUAL')
- chk_recorrente_frequencia: If recorrente = true, frequencia_recorrencia must not be null

**Recurring Transaction Support:**
- Parent transaction (transacao_pai_id = NULL, recorrente = true)
- Child instances reference parent via transacao_pai_id
- Supports various recurrence frequencies
- Optional end date for recurrence

## Entity Relationships

### ERD Overview
```
users ←──┐
         │
         ├──→ user_roles ←──→ roles ←──→ role_permissions ←──→ permissions
         │
         ├──→ refresh_tokens
         │
         └──→ audit_logs

categorias (self-referencing for hierarchy)
    ↑
    │
transacoes ──→ clientes
```

### Key Relationships

1. **Users & Roles** (Many-to-Many)
   - Users can have multiple roles
   - Roles can be assigned to multiple users
   - Junction table: user_roles

2. **Roles & Permissions** (Many-to-Many)
   - Roles can have multiple permissions
   - Permissions can belong to multiple roles
   - Junction table: role_permissions

3. **Users & Refresh Tokens** (One-to-Many)
   - Users can have multiple active refresh tokens
   - Each token belongs to one user
   - Cascade delete when user is deleted

4. **Users & Audit Logs** (One-to-Many)
   - Users can have multiple audit log entries
   - Set NULL on user deletion to preserve audit trail

5. **Categorias** (Self-Referencing Hierarchy)
   - Categories can have parent categories
   - Supports unlimited nesting depth
   - categoria_pai_id references categorias(id)

6. **Transacoes & Categorias** (Many-to-One)
   - Each transaction belongs to one category
   - Categories can have multiple transactions

7. **Transacoes & Clientes** (Many-to-One, Optional)
   - Transactions can optionally be linked to a client
   - Clients can have multiple transactions

8. **Transacoes** (Self-Referencing for Recurring)
   - Recurring transactions reference a parent transaction
   - Parent transaction has transacao_pai_id = NULL
   - Child instances link via transacao_pai_id

## Audit Fields

All main tables include standard audit fields:
- **created_at**: Record creation timestamp (auto-set)
- **updated_at**: Last modification timestamp
- **created_by**: Username who created the record
- **updated_by**: Username who last modified
- **version**: Optimistic locking version number

## Security Considerations

### Password Security
- Passwords stored using BCrypt hashing (strength factor 10)
- Never store passwords in plain text
- Minimum password requirements enforced in application layer

### Account Lockout
- Failed login attempts tracked in `login_attempts`
- Account locked temporarily via `locked_until` timestamp
- Automatic unlock after expiry

### Token Management
- JWT tokens for authentication
- Refresh tokens for session extension
- Token revocation support
- Device and IP tracking for security audits

### Audit Trail
- All critical actions logged in audit_logs
- Immutable audit records (no updates/deletes)
- Captures before/after values for changes

## Performance Optimization

### Indexes
- Strategic indexes on frequently queried columns
- Composite indexes for common query patterns
- Partial indexes for conditional queries (e.g., WHERE cpf IS NOT NULL)

### Constraints
- Foreign key constraints ensure referential integrity
- Check constraints enforce business rules at database level
- Unique constraints prevent duplicate data

### Query Optimization Tips
1. Use indexes for filtering (WHERE clauses)
2. Avoid SELECT * - specify needed columns
3. Use EXPLAIN ANALYZE to review query plans
4. Consider materialized views for complex reports
5. Partition large tables (transacoes) by date range

## Backup and Maintenance

### Recommended Practices
1. **Daily Backups**: Automated full database backups
2. **Point-in-Time Recovery**: Enable WAL archiving
3. **Regular Vacuum**: Run VACUUM ANALYZE weekly
4. **Index Maintenance**: REINDEX monthly
5. **Audit Log Archival**: Archive old logs quarterly

### Data Retention
- Transactions: Indefinite (for financial records)
- Audit logs: 7 years (compliance requirement)
- Refresh tokens: Auto-delete after expiry
- Inactive users: Review annually

## Migration Commands

### Apply Migrations
```bash
# Flyway will run automatically on application startup
# Or run manually:
mvn flyway:migrate

# Check migration status
mvn flyway:info
```

### Rollback
Flyway doesn't support automatic rollback. Create undo migrations manually.

### Validate Schema
```bash
mvn flyway:validate
```

## Database Statistics

**Total Tables:** 11
- Authentication: 6 tables
- Business Domain: 3 tables
- Audit: 1 table
- Support: 1 table (refresh_tokens)

**Total Indexes:** 45+
**Total Constraints:** 25+
**Default Data Rows:**
- Categories: 29 (10 income, 17 expense, 2 subcategories)
- Clients: 5 (3 PF, 2 PJ)
- Transactions: 9 sample transactions
- Users: 4 (admin + 3 demo users)
- Roles: 4
- Permissions: 32

## Troubleshooting

### Common Issues

**Issue: Flyway migration fails**
```sql
-- Check migration history
SELECT * FROM flyway_schema_history;

-- Repair if needed
mvn flyway:repair
```

**Issue: Foreign key constraint violation**
```sql
-- Check orphaned records
SELECT * FROM transacoes WHERE categoria_id NOT IN (SELECT id FROM categorias);
```

**Issue: Slow queries on transactions**
```sql
-- Analyze query performance
EXPLAIN ANALYZE SELECT * FROM transacoes WHERE data_transacao > '2024-01-01';

-- Rebuild indexes if needed
REINDEX TABLE transacoes;
```

## Database Connection Info

**Default Configuration (Development):**
- Host: localhost
- Port: 5432
- Database: financas_db
- Username: financas_user
- Password: financas_password

**Production:** Use environment variables for sensitive data.

---

**Last Updated:** 2025-10-16
**Schema Version:** V4
**Status:** ✅ Complete and ready for use
