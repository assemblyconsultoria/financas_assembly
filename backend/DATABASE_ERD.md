# Financial Assembly - Entity Relationship Diagram

## Full Database Schema

```mermaid
erDiagram
    %% Authentication & Authorization
    users ||--o{ user_roles : has
    roles ||--o{ user_roles : assigned
    roles ||--o{ role_permissions : has
    permissions ||--o{ role_permissions : granted
    users ||--o{ refresh_tokens : owns
    users ||--o{ audit_logs : performs

    %% Business Domain
    categorias ||--o{ categorias : "parent of"
    categorias ||--o{ transacoes : categorizes
    clientes ||--o{ transacoes : "involved in"
    transacoes ||--o{ transacoes : "parent of (recurring)"

    %% Users Table
    users {
        bigserial id PK
        varchar name
        varchar email UK
        varchar password
        boolean active
        boolean email_verified
        varchar email_verification_token
        timestamp email_verification_expiry
        varchar password_reset_token
        timestamp password_reset_expiry
        timestamp last_login
        integer login_attempts
        timestamp locked_until
        timestamp created_at
        timestamp updated_at
        varchar created_by
        varchar updated_by
        bigint version
    }

    %% Roles Table
    roles {
        bigserial id PK
        varchar name UK
        varchar description
        boolean active
        timestamp created_at
        timestamp updated_at
        bigint version
    }

    %% User Roles Junction
    user_roles {
        bigint user_id FK
        bigint role_id FK
        timestamp created_at
    }

    %% Permissions Table
    permissions {
        bigserial id PK
        varchar name UK
        varchar resource
        varchar action
        varchar description
        boolean active
        timestamp created_at
        timestamp updated_at
        bigint version
    }

    %% Role Permissions Junction
    role_permissions {
        bigint role_id FK
        bigint permission_id FK
        timestamp created_at
    }

    %% Refresh Tokens
    refresh_tokens {
        bigserial id PK
        bigint user_id FK
        varchar token UK
        timestamp expiry_date
        boolean revoked
        timestamp revoked_at
        varchar device_info
        varchar ip_address
        timestamp created_at
    }

    %% Audit Logs
    audit_logs {
        bigserial id PK
        bigint user_id FK
        varchar action
        varchar entity_type
        bigint entity_id
        text old_value
        text new_value
        varchar ip_address
        varchar user_agent
        timestamp created_at
    }

    %% Categorias Table
    categorias {
        bigserial id PK
        varchar nome UK
        varchar descricao
        varchar tipo "CHECK(RECEITA,DESPESA)"
        varchar cor
        varchar icone
        boolean ativa
        bigint categoria_pai_id FK
        timestamp created_at
        timestamp updated_at
        varchar created_by
        varchar updated_by
        bigint version
    }

    %% Clientes Table (Single Table Inheritance)
    clientes {
        bigserial id PK
        varchar tipo_cliente "CHECK(PF,PJ)"
        varchar nome
        varchar email
        varchar telefone
        varchar endereco
        varchar cidade
        varchar estado
        varchar cep
        boolean ativo
        varchar observacoes
        varchar cpf UK "PF only"
        varchar rg "PF only"
        date data_nascimento "PF only"
        varchar profissao "PF only"
        varchar estado_civil "PF only"
        varchar cnpj UK "PJ only"
        varchar razao_social "PJ only"
        varchar nome_fantasia "PJ only"
        varchar inscricao_estadual "PJ only"
        varchar inscricao_municipal "PJ only"
        date data_fundacao "PJ only"
        varchar setor "PJ only"
        varchar porte "PJ only"
        varchar responsavel_nome "PJ only"
        varchar responsavel_email "PJ only"
        varchar responsavel_telefone "PJ only"
        timestamp created_at
        timestamp updated_at
        varchar created_by
        varchar updated_by
        bigint version
    }

    %% Transacoes Table
    transacoes {
        bigserial id PK
        varchar tipo "CHECK(RECEITA,DESPESA)"
        numeric valor "CHECK > 0"
        date data_transacao
        varchar descricao
        varchar observacoes
        bigint categoria_id FK
        bigint cliente_id FK
        varchar metodo_pagamento
        varchar status
        boolean efetivada
        timestamp data_efetivacao
        varchar numero_documento
        boolean recorrente
        varchar frequencia_recorrencia
        date data_fim_recorrencia
        bigint transacao_pai_id FK
        timestamp created_at
        timestamp updated_at
        varchar created_by
        varchar updated_by
        bigint version
    }
```

## Simplified View - Authentication

```mermaid
erDiagram
    users ||--o{ user_roles : "has"
    roles ||--o{ user_roles : "assigned to"
    roles ||--o{ role_permissions : "has"
    permissions ||--o{ role_permissions : "granted to"
    users ||--o{ refresh_tokens : "owns"

    users {
        bigserial id
        varchar name
        varchar email
        varchar password
        boolean active
    }

    roles {
        bigserial id
        varchar name
        varchar description
    }

    permissions {
        bigserial id
        varchar name
        varchar resource
        varchar action
    }

    user_roles {
        bigint user_id
        bigint role_id
    }

    role_permissions {
        bigint role_id
        bigint permission_id
    }

    refresh_tokens {
        bigserial id
        bigint user_id
        varchar token
        timestamp expiry_date
    }
```

## Simplified View - Business Domain

```mermaid
erDiagram
    categorias ||--o{ categorias : "parent of"
    categorias ||--o{ transacoes : "categorizes"
    clientes ||--o{ transacoes : "involved in"
    transacoes ||--o{ transacoes : "recurring parent"

    categorias {
        bigserial id
        varchar nome
        varchar tipo
        varchar cor
        varchar icone
        bigint categoria_pai_id
    }

    clientes {
        bigserial id
        varchar tipo_cliente
        varchar nome
        varchar email
        varchar cpf
        varchar cnpj
    }

    transacoes {
        bigserial id
        varchar tipo
        numeric valor
        date data_transacao
        varchar descricao
        bigint categoria_id
        bigint cliente_id
        varchar metodo_pagamento
        varchar status
        boolean recorrente
        bigint transacao_pai_id
    }
```

## Relationship Details

### Users & Authentication

| Relationship | Type | Description |
|--------------|------|-------------|
| users → user_roles | 1:N | A user can have multiple roles |
| roles → user_roles | 1:N | A role can be assigned to multiple users |
| roles → role_permissions | 1:N | A role can have multiple permissions |
| permissions → role_permissions | 1:N | A permission can belong to multiple roles |
| users → refresh_tokens | 1:N | A user can have multiple refresh tokens |
| users → audit_logs | 1:N | A user can perform multiple actions |

**Key Points:**
- Many-to-many between users and roles (via user_roles)
- Many-to-many between roles and permissions (via role_permissions)
- Cascade delete on user_roles and role_permissions
- Set NULL on audit_logs.user_id to preserve audit trail

### Business Domain

| Relationship | Type | Description |
|--------------|------|-------------|
| categorias → categorias | 1:N | Self-referencing for category hierarchy |
| categorias → transacoes | 1:N | A category can have multiple transactions |
| clientes → transacoes | 1:N | A client can have multiple transactions |
| transacoes → transacoes | 1:N | Self-referencing for recurring transactions |

**Key Points:**
- Optional relationship between transacoes and clientes (nullable)
- Self-referencing relationships support unlimited nesting
- Foreign key constraints maintain referential integrity

## Inheritance Strategy

### Clientes Table - Single Table Inheritance

The `clientes` table uses Single Table Inheritance (STI) to store both Pessoa Física (PF) and Pessoa Jurídica (PJ):

```
clientes
├── tipo_cliente: 'PF' (discriminator)
│   ├── Common fields: nome, email, telefone, endereco...
│   └── PF-specific: cpf, rg, data_nascimento, profissao, estado_civil
│
└── tipo_cliente: 'PJ' (discriminator)
    ├── Common fields: nome, email, telefone, endereco...
    └── PJ-specific: cnpj, razao_social, nome_fantasia, setor, porte...
```

**Advantages:**
- Single table for all client types
- Easy to query all clients
- Foreign keys work seamlessly

**Constraints:**
- `tipo_cliente` CHECK ('PF', 'PJ')
- If PF, cpf must not be null
- If PJ, cnpj must not be null

## Indexes Overview

### Users & Auth Indexes
- `idx_user_email` on users(email)
- `idx_user_active` on users(active)
- `idx_refresh_token_token` on refresh_tokens(token)
- `idx_refresh_token_expiry` on refresh_tokens(expiry_date)

### Business Domain Indexes
- `idx_cliente_tipo` on clientes(tipo_cliente)
- `idx_cliente_cpf` on clientes(cpf) WHERE cpf IS NOT NULL
- `idx_cliente_cnpj` on clientes(cnpj) WHERE cnpj IS NOT NULL
- `idx_transacao_data` on transacoes(data_transacao DESC)
- `idx_transacao_tipo` on transacoes(tipo)
- `idx_categoria_tipo` on categorias(tipo)

### Composite Indexes
- `idx_transacao_data_tipo` on (data_transacao DESC, tipo)
- `idx_transacao_cliente_data` on (cliente_id, data_transacao DESC)
- `idx_transacao_categoria_data` on (categoria_id, data_transacao DESC)

## Security Model

### Role Hierarchy

```
ROLE_ADMIN (Full Access)
    ├── All permissions
    └── System administration

ROLE_MANAGER (Business Management)
    ├── CLIENTE: CREATE, READ, UPDATE, DELETE, LIST
    ├── TRANSACAO: CREATE, READ, UPDATE, DELETE, LIST, APPROVE, CANCEL
    ├── CATEGORIA: All operations
    └── REPORT: VIEW, EXPORT, CREATE

ROLE_USER (Standard User)
    ├── CLIENTE: CREATE, READ, UPDATE, LIST
    ├── TRANSACAO: CREATE, READ, UPDATE, LIST
    ├── CATEGORIA: READ, LIST
    └── REPORT: VIEW, EXPORT

ROLE_VIEWER (Read-Only)
    ├── All resources: READ, LIST, VIEW only
    └── No create, update, or delete permissions
```

### Permission Format

Permissions follow the pattern: `{RESOURCE}_{ACTION}`

**Resources:**
- CLIENTE
- TRANSACAO
- CATEGORIA
- USER
- REPORT
- SYSTEM

**Actions:**
- CREATE
- READ
- UPDATE
- DELETE
- LIST
- APPROVE (transacoes only)
- CANCEL (transacoes only)
- VIEW (reports)
- EXPORT (reports)
- SETTINGS (system)
- AUDIT (system)
- BACKUP (system)

## Data Flow Example

### Creating a Transaction

```mermaid
sequenceDiagram
    participant U as User
    participant A as Auth System
    participant T as Transacoes
    participant C as Categorias
    participant CL as Clientes
    participant AL as Audit Logs

    U->>A: Login (email, password)
    A->>U: JWT Token + Refresh Token
    U->>T: Create Transaction
    T->>A: Verify Token & Permissions
    A->>T: Authorized (TRANSACAO_CREATE)
    T->>C: Validate categoria_id exists
    C->>T: Valid
    T->>CL: Validate cliente_id exists (optional)
    CL->>T: Valid
    T->>T: Insert transaction record
    T->>AL: Log action
    T->>U: Transaction created
```

### User Authentication Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant A as Auth Controller
    participant U as Users Table
    participant UR as User Roles
    participant RT as Refresh Tokens

    C->>A: POST /auth/login
    A->>U: Find user by email
    U->>A: User found
    A->>A: Verify password (BCrypt)
    A->>UR: Get user roles
    UR->>A: Roles list
    A->>A: Generate JWT Token
    A->>RT: Create refresh token
    RT->>A: Token saved
    A->>U: Update last_login
    A->>C: Return tokens + user info
```

---

**Tools to View ERD:**
- GitHub (renders Mermaid automatically)
- VS Code (with Mermaid extension)
- Mermaid Live Editor: https://mermaid.live
- IntelliJ IDEA (with Mermaid plugin)

**Export Options:**
- Copy Mermaid code to https://mermaid.live
- Export as PNG, SVG, or PDF
- Use in documentation, presentations

---

**Last Updated:** 2025-10-16
**Schema Version:** V4
