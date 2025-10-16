# JPA Entities Implementation Summary

## Overview
This document summarizes all JPA entity classes implemented for the Financial Assembly application, mapping to the PostgreSQL database schema created by Flyway migrations.

## Entity Overview

### Total Entities: 11

**Authentication & Authorization (6 entities):**
- User
- Role
- Permission
- RefreshToken
- AuditLog
- BaseEntity (MappedSuperclass)

**Business Domain (5 entities):**
- Cliente (abstract base class)
- PessoaFisica (extends Cliente)
- Empresa (extends Cliente)
- Categoria
- Transacao

## Entity Details

### 1. BaseEntity (MappedSuperclass)

**Location:** `com.financas.assembly.entity.BaseEntity`

**Purpose:** Abstract base class providing audit fields to all entities.

**Fields:**
- `createdAt` - Timestamp of record creation (@CreatedDate)
- `updatedAt` - Timestamp of last modification (@LastModifiedDate)
- `createdBy` - Username who created the record (@CreatedBy)
- `updatedBy` - Username who last modified (@LastModifiedBy)
- `version` - Optimistic locking version (@Version)

**Features:**
- Uses Spring Data JPA auditing (@EntityListeners(AuditingEntityListener.class))
- Automatic timestamp and user tracking
- Optimistic locking support

---

### 2. User

**Location:** `com.financas.assembly.entity.User`

**Purpose:** Represents system users with authentication credentials.

**Table:** `users`

**Key Fields:**
- `id` (Long) - Primary key
- `name` (String) - User's full name
- `email` (String) - Email address (unique, used for login)
- `password` (String) - BCrypt hashed password
- `active` (Boolean) - Account active status
- `emailVerified` (Boolean) - Email verification status
- `emailVerificationToken` (String) - Token for email verification
- `passwordResetToken` (String) - Token for password reset
- `lastLogin` (LocalDateTime) - Last successful login
- `loginAttempts` (Integer) - Failed login counter
- `lockedUntil` (LocalDateTime) - Account lock expiry

**Relationships:**
- Many-to-Many with Role (via user_roles junction table)
- One-to-Many with RefreshToken

**Implements:** UserDetails (Spring Security)

**Special Features:**
- Implements Spring Security UserDetails interface
- `getAuthorities()` - Returns roles and permissions as GrantedAuthority
- `incrementLoginAttempts()` - Locks account after 5 failed attempts (30 minutes)
- `resetLoginAttempts()` - Resets login attempts and unlocks account
- `hasRole(String)` - Check if user has specific role
- `hasPermission(String)` - Check if user has specific permission

**Indexes:**
- idx_user_email (email)
- idx_user_active (active)

---

### 3. Role

**Location:** `com.financas.assembly.entity.Role`

**Purpose:** Defines user roles for RBAC (Role-Based Access Control).

**Table:** `roles`

**Key Fields:**
- `id` (Long) - Primary key
- `name` (String) - Role name (unique, e.g., ROLE_ADMIN)
- `description` (String) - Role description
- `active` (Boolean) - Role active status

**Relationships:**
- Many-to-Many with User (via user_roles)
- Many-to-Many with Permission (via role_permissions)

**Helper Methods:**
- `addPermission(Permission)` - Adds permission to role
- `removePermission(Permission)` - Removes permission from role
- `hasPermission(String)` - Checks if role has permission

---

### 4. Permission

**Location:** `com.financas.assembly.entity.Permission`

**Purpose:** Defines granular permissions for resources and actions.

**Table:** `permissions`

**Key Fields:**
- `id` (Long) - Primary key
- `name` (String) - Permission name (unique)
- `resource` (String) - Resource type (e.g., CLIENTE, TRANSACAO)
- `action` (String) - Action type (e.g., CREATE, READ, UPDATE)
- `description` (String) - Permission description
- `active` (Boolean) - Permission active status

**Relationships:**
- Many-to-Many with Role (via role_permissions)

**Constraints:**
- Unique constraint on (resource, action)

**Indexes:**
- idx_permission_resource (resource)
- idx_permission_active (active)

**Constructor:**
```java
new Permission("CLIENTE", "CREATE", "Create new clients")
// Automatically generates name: CLIENTE_CREATE
```

---

### 5. RefreshToken

**Location:** `com.financas.assembly.entity.RefreshToken`

**Purpose:** Manages JWT refresh tokens for session management.

**Table:** `refresh_tokens`

**Key Fields:**
- `id` (Long) - Primary key
- `user` (User) - User who owns the token
- `token` (String) - Refresh token value (unique)
- `expiryDate` (LocalDateTime) - Token expiry date
- `revoked` (Boolean) - Revocation status
- `revokedAt` (LocalDateTime) - Revocation timestamp
- `deviceInfo` (String) - Device information
- `ipAddress` (String) - Client IP address
- `createdAt` (LocalDateTime) - Token creation timestamp

**Relationships:**
- Many-to-One with User

**Helper Methods:**
- `isExpired()` - Checks if token is expired
- `isValid()` - Checks if token is not revoked and not expired
- `revoke()` - Revokes the token

**Indexes:**
- idx_refresh_token_user (user_id)
- idx_refresh_token_token (token)
- idx_refresh_token_expiry (expiry_date)
- idx_refresh_token_revoked (revoked)

---

### 6. AuditLog

**Location:** `com.financas.assembly.entity.AuditLog`

**Purpose:** Tracks system actions for compliance and security.

**Table:** `audit_logs`

**Key Fields:**
- `id` (Long) - Primary key
- `user` (User) - User who performed action
- `action` (String) - Action performed
- `entityType` (String) - Type of entity affected
- `entityId` (Long) - ID of entity affected
- `oldValue` (String) - Previous value (JSON)
- `newValue` (String) - New value (JSON)
- `ipAddress` (String) - Client IP address
- `userAgent` (String) - Browser user agent
- `createdAt` (LocalDateTime) - Action timestamp

**Relationships:**
- Many-to-One with User (nullable - SET NULL on delete)

**Builder Pattern:**
```java
AuditLog log = AuditLog.builder()
    .user(user)
    .action("CREATE")
    .entityType("Cliente")
    .entityId(clienteId)
    .newValue(jsonValue)
    .ipAddress("192.168.1.1")
    .build();
```

**Indexes:**
- idx_audit_log_user (user_id)
- idx_audit_log_action (action)
- idx_audit_log_entity (entity_type, entity_id)
- idx_audit_log_created_at (created_at DESC)

---

### 7. Cliente (Abstract)

**Location:** `com.financas.assembly.entity.Cliente`

**Purpose:** Base entity for all clients using Single Table Inheritance.

**Table:** `clientes`

**Inheritance Strategy:** SINGLE_TABLE

**Discriminator Column:** `tipo_cliente` (PF or PJ)

**Key Fields:**
- `id` (Long) - Primary key
- `nome` (String) - Client name
- `email` (String) - Email address
- `telefone` (String) - Phone number
- `endereco` (String) - Street address
- `cidade` (String) - City
- `estado` (String) - State code (2 chars)
- `cep` (String) - Postal code
- `ativo` (Boolean) - Active status
- `observacoes` (String) - Notes

**Relationships:**
- One-to-Many with Transacao

**Subclasses:**
- PessoaFisica (PF)
- Empresa (PJ)

---

### 8. PessoaFisica (Pessoa Física - Individual)

**Location:** `com.financas.assembly.entity.PessoaFisica`

**Purpose:** Represents individual clients (natural persons).

**Discriminator Value:** "PF"

**Additional Fields:**
- `cpf` (String) - Brazilian tax ID (11 digits, unique)
- `rg` (String) - ID document number (9 digits)
- `dataNascimento` (LocalDate) - Birth date
- `profissao` (String) - Profession
- `estadoCivil` (String) - Marital status

**Validations:**
- CPF required and must match pattern \\d{11}
- RG must match pattern \\d{9}
- Birth date must be in the past

---

### 9. Empresa (Pessoa Jurídica - Company)

**Location:** `com.financas.assembly.entity.Empresa`

**Purpose:** Represents corporate clients (legal entities).

**Discriminator Value:** "PJ"

**Additional Fields:**
- `cnpj` (String) - Brazilian tax ID (14 digits, unique)
- `razaoSocial` (String) - Legal company name
- `nomeFantasia` (String) - Trade name
- `inscricaoEstadual` (String) - State tax registration
- `inscricaoMunicipal` (String) - City tax registration
- `dataFundacao` (LocalDate) - Foundation date
- `setor` (String) - Business sector
- `porte` (String) - Company size
- `responsavelNome` (String) - Contact person name
- `responsavelEmail` (String) - Contact person email
- `responsavelTelefone` (String) - Contact person phone

**Validations:**
- CNPJ required and must match pattern \\d{14}

---

### 10. Categoria

**Location:** `com.financas.assembly.entity.Categoria`

**Purpose:** Transaction categories for income/expense classification.

**Table:** `categorias`

**Key Fields:**
- `id` (Long) - Primary key
- `nome` (String) - Category name (unique)
- `descricao` (String) - Category description
- `tipo` (TipoCategoria) - Category type (RECEITA/DESPESA)
- `cor` (String) - Color code (hex)
- `icone` (String) - Material icon name
- `ativa` (Boolean) - Active status
- `categoriaPai` (Categoria) - Parent category (for hierarchy)

**Enums:**
```java
public enum TipoCategoria {
    RECEITA,  // Income
    DESPESA   // Expense
}
```

**Relationships:**
- Self-referencing (parent-child hierarchy)

**Indexes:**
- idx_categoria_pai (categoria_pai_id)
- idx_categoria_tipo (tipo)
- idx_categoria_ativa (ativa)

---

### 11. Transacao

**Location:** `com.financas.assembly.entity.Transacao`

**Purpose:** Financial transactions tracking income and expenses.

**Table:** `transacoes`

**Key Fields:**
- `id` (Long) - Primary key
- `tipo` (TipoTransacao) - Transaction type (RECEITA/DESPESA)
- `valor` (BigDecimal) - Transaction amount (precision 12, scale 2)
- `dataTransacao` (LocalDate) - Transaction date
- `descricao` (String) - Description
- `observacoes` (String) - Additional notes
- `categoria` (Categoria) - Category (required)
- `cliente` (Cliente) - Client (optional)
- `metodoPagamento` (MetodoPagamento) - Payment method
- `status` (StatusTransacao) - Transaction status
- `efetivada` (Boolean) - Completion status
- `dataEfetivacao` (LocalDateTime) - Completion timestamp
- `numeroDocumento` (String) - Document number
- `recorrente` (Boolean) - Recurring flag
- `frequenciaRecorrencia` (FrequenciaRecorrencia) - Recurrence frequency
- `dataFimRecorrencia` (LocalDate) - Recurrence end date
- `transacaoPai` (Transacao) - Parent transaction

**Enums:**
```java
public enum TipoTransacao {
    RECEITA,  // Income
    DESPESA   // Expense
}

public enum MetodoPagamento {
    DINHEIRO, CARTAO_CREDITO, CARTAO_DEBITO,
    TRANSFERENCIA, PIX, BOLETO, CHEQUE, OUTRO
}

public enum StatusTransacao {
    PENDENTE, CONFIRMADA, CANCELADA, ESTORNADA
}

public enum FrequenciaRecorrencia {
    DIARIA, SEMANAL, QUINZENAL, MENSAL,
    BIMESTRAL, TRIMESTRAL, SEMESTRAL, ANUAL
}
```

**Relationships:**
- Many-to-One with Categoria
- Many-to-One with Cliente (optional)
- Self-referencing (parent-child for recurring)

**Indexes:**
- idx_transacao_data (data_transacao DESC)
- idx_transacao_tipo (tipo)
- idx_transacao_cliente (cliente_id)
- idx_transacao_categoria (categoria_id)

---

## Inheritance Strategies

### Single Table Inheritance (Cliente)

```
clientes table
├── tipo_cliente = 'PF' → PessoaFisica
│   └── Fields: cpf, rg, dataNascimento, profissao, estadoCivil
└── tipo_cliente = 'PJ' → Empresa
    └── Fields: cnpj, razaoSocial, nomeFantasia, setor, porte, etc.
```

**Advantages:**
- Single table for queries
- Simple foreign keys
- Good performance

**Disadvantages:**
- Nullable columns for subclass-specific fields
- All subclass fields in one table

---

## Validation Summary

### Bean Validation Annotations Used

- **@NotBlank** - Non-empty strings
- **@NotNull** - Non-null values
- **@Email** - Valid email format
- **@Size** - String length constraints
- **@Pattern** - Regex pattern matching (CPF, CNPJ, RG)
- **@Digits** - Numeric precision
- **@DecimalMin** - Minimum decimal value
- **@Past** - Date in the past

### Example Validations

```java
@NotBlank(message = "Nome é obrigatório")
@Size(min = 3, max = 200)
private String nome;

@Email(message = "Email inválido")
private String email;

@Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
private String cpf;

@DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
@Digits(integer = 10, fraction = 2)
private BigDecimal valor;
```

---

## Relationship Summary

### Many-to-Many Relationships

1. **User ↔ Role** (via user_roles)
2. **Role ↔ Permission** (via role_permissions)

### One-to-Many Relationships

1. **User → RefreshToken**
2. **User → AuditLog**
3. **Cliente → Transacao**
4. **Categoria → Transacao**

### Self-Referencing Relationships

1. **Categoria → Categoria** (parent-child hierarchy)
2. **Transacao → Transacao** (recurring transactions)

---

## Features Implementation

### 1. Spring Security Integration

**User entity implements UserDetails:**
- getAuthorities() - Returns roles + permissions
- getUsername() - Returns email
- isAccountNonLocked() - Checks lockedUntil
- isEnabled() - Checks active && emailVerified

### 2. Auditing

**BaseEntity provides automatic auditing:**
- Who created/updated the record
- When it was created/updated
- Optimistic locking with version

### 3. Soft Delete

**Entities support soft delete:**
- Cliente.ativo
- Categoria.ativa
- User.active
- Role.active
- Permission.active

### 4. Account Security

**User entity features:**
- Login attempt tracking
- Automatic account locking (5 failed attempts)
- Password reset token
- Email verification

### 5. Token Management

**RefreshToken features:**
- Expiry checking
- Manual revocation
- Device and IP tracking

### 6. Audit Trail

**AuditLog features:**
- Action tracking
- Before/after values (JSON)
- IP and user agent tracking
- Immutable records

---

## Next Steps

### Repository Layer
Create Spring Data JPA repositories for each entity:
- UserRepository
- RoleRepository
- PermissionRepository
- RefreshTokenRepository
- AuditLogRepository
- ClienteRepository
- CategoriaRepository
- TransacaoRepository

### Service Layer
Implement business logic:
- UserService
- AuthService
- ClienteService
- TransacaoService
- CategoriaService
- AuditService

### Controller Layer
Create REST controllers:
- AuthController
- UserController
- ClienteController
- TransacaoController
- CategoriaController

---

## File Locations

All entities are located in:
```
backend/src/main/java/com/financas/assembly/entity/
├── BaseEntity.java
├── User.java
├── Role.java
├── Permission.java
├── RefreshToken.java
├── AuditLog.java
├── Cliente.java
├── PessoaFisica.java
├── Empresa.java
├── Categoria.java
└── Transacao.java
```

---

**Status:** ✅ All JPA entities implemented and ready for use
**Last Updated:** 2025-10-16
**Total Files:** 11 entity classes
**Database Compatibility:** PostgreSQL 16
