# Spring Data JPA Repositories Implementation

## Overview
This document describes all Spring Data JPA repository interfaces implemented for the Financial Assembly application. These repositories provide data access methods with custom queries for complex operations.

## Repository Summary

### Total Repositories: 10

**Authentication & Authorization (5 repositories):**
- UserRepository
- RoleRepository
- PermissionRepository
- RefreshTokenRepository
- AuditLogRepository

**Business Domain (5 repositories):**
- ClienteRepository
- PessoaFisicaRepository (specific queries for individuals)
- EmpresaRepository (specific queries for companies)
- CategoriaRepository
- TransacaoRepository

---

## Authentication Repositories

### 1. UserRepository

**Location:** `com.financas.assembly.repository.UserRepository`

**Extends:** JpaRepository<User, Long>, JpaSpecificationExecutor<User>

**Purpose:** User management and authentication queries.

**Key Methods:**

#### Finder Methods
```java
Optional<User> findByEmail(String email)
Optional<User> findByEmailWithRoles(String email)  // Eager fetch roles
Optional<User> findByEmailVerificationToken(String token)
Optional<User> findByPasswordResetToken(String token)
List<User> findByActiveTrue()
List<User> findByActiveFalse()
List<User> findByEmailVerified(Boolean verified)
List<User> findByRoleName(String roleName)
```

#### Custom Queries
```java
// Find users with expired tokens
List<User> findUsersWithExpiredPasswordResetTokens(LocalDateTime now)
List<User> findUsersWithExpiredEmailVerificationTokens(LocalDateTime now)

// Find locked/unlocked users
List<User> findLockedUsers(LocalDateTime now)
List<User> findUsersWithExpiredLocks(LocalDateTime now)

// Search
List<User> searchByNameOrEmail(String searchTerm)
```

#### Update Operations
```java
@Modifying
void updateLastLogin(Long userId, LocalDateTime timestamp)

@Modifying
void resetLoginAttempts(Long userId)

@Modifying
void incrementLoginAttempts(Long userId)

@Modifying
void clearExpiredEmailVerificationTokens(LocalDateTime now)

@Modifying
void clearExpiredPasswordResetTokens(LocalDateTime now)
```

#### Count Methods
```java
long countByActiveTrue()
long countByEmailVerified(Boolean verified)
boolean existsByEmail(String email)
```

**Usage Example:**
```java
// Find user by email with roles
Optional<User> user = userRepository.findByEmailWithRoles("user@example.com");

// Reset login attempts
userRepository.resetLoginAttempts(userId);

// Search users
List<User> users = userRepository.searchByNameOrEmail("john");
```

---

### 2. RoleRepository

**Location:** `com.financas.assembly.repository.RoleRepository`

**Extends:** JpaRepository<Role, Long>

**Purpose:** Role management for RBAC.

**Key Methods:**

```java
// Basic finders
Optional<Role> findByName(String name)
Optional<Role> findByNameWithPermissions(String name)  // Eager fetch
List<Role> findByActiveTrue()
List<Role> findByActiveFalse()

// Bulk operations
Set<Role> findByIdInWithPermissions(Set<Long> ids)
List<Role> findAllWithPermissions()

// Advanced queries
List<Role> findByPermissionName(String permissionName)
List<Role> searchByNameOrDescription(String searchTerm)

// Counts
long countByActiveTrue()
boolean existsByName(String name)
```

**Usage Example:**
```java
// Find role with permissions
Optional<Role> adminRole = roleRepository.findByNameWithPermissions("ROLE_ADMIN");

// Find roles with specific permission
List<Role> roles = roleRepository.findByPermissionName("CLIENTE_CREATE");
```

---

### 3. PermissionRepository

**Location:** `com.financas.assembly.repository.PermissionRepository`

**Extends:** JpaRepository<Permission, Long>

**Purpose:** Permission management for fine-grained access control.

**Key Methods:**

```java
// Basic finders
Optional<Permission> findByName(String name)
Optional<Permission> findByResourceAndAction(String resource, String action)
List<Permission> findByActiveTrue()
List<Permission> findByResource(String resource)
List<Permission> findByResourceAndActive(String resource, Boolean active)
List<Permission> findByAction(String action)

// Lookup helpers
List<String> findAllResources()  // Distinct resources
List<String> findAllActions()    // Distinct actions

// Role-based queries
List<Permission> findByRoleId(Long roleId)
List<Permission> findNotAssignedToRole(Long roleId)

// Search
List<Permission> searchByNameOrDescription(String searchTerm)

// Counts
long countByActiveTrue()
long countByResource(String resource)
boolean existsByName(String name)
boolean existsByResourceAndAction(String resource, String action)
```

**Usage Example:**
```java
// Find permission by resource and action
Optional<Permission> perm = permissionRepository
    .findByResourceAndAction("CLIENTE", "CREATE");

// Get all resources
List<String> resources = permissionRepository.findAllResources();

// Find unassigned permissions
List<Permission> unassigned = permissionRepository.findNotAssignedToRole(roleId);
```

---

### 4. RefreshTokenRepository

**Location:** `com.financas.assembly.repository.RefreshTokenRepository`

**Extends:** JpaRepository<RefreshToken, Long>

**Purpose:** JWT refresh token management and cleanup.

**Key Methods:**

```java
// Token lookup
Optional<RefreshToken> findByToken(String token)
List<RefreshToken> findByUserId(Long userId)
List<RefreshToken> findValidTokensByUserId(Long userId, LocalDateTime now)

// Status queries
List<RefreshToken> findExpiredTokens(LocalDateTime now)
List<RefreshToken> findByRevokedTrue()
List<RefreshToken> findByDeviceInfo(String deviceInfo)
List<RefreshToken> findByIpAddress(String ipAddress)

// Bulk operations
@Modifying
void deleteExpiredTokens(LocalDateTime now)

@Modifying
void deleteOldRevokedTokens(LocalDateTime date)

@Modifying
void revokeAllUserTokens(Long userId, LocalDateTime now)

@Modifying
void revokeExpiredTokens(LocalDateTime now)

// Counts
long countValidTokensByUserId(Long userId, LocalDateTime now)
long countByExpiryDateBefore(LocalDateTime now)
long countByRevokedTrue()

// Cleanup
void deleteByUserId(Long userId)
```

**Usage Example:**
```java
// Find valid tokens for user
LocalDateTime now = LocalDateTime.now();
List<RefreshToken> tokens = refreshTokenRepository
    .findValidTokensByUserId(userId, now);

// Revoke all user tokens
refreshTokenRepository.revokeAllUserTokens(userId, now);

// Cleanup expired tokens
refreshTokenRepository.deleteExpiredTokens(now);
```

---

### 5. AuditLogRepository

**Location:** `com.financas.assembly.repository.AuditLogRepository`

**Extends:** JpaRepository<AuditLog, Long>

**Purpose:** Audit trail queries and reporting.

**Key Methods:**

```java
// Basic queries with pagination
Page<AuditLog> findByUserId(Long userId, Pageable pageable)
Page<AuditLog> findByAction(String action, Pageable pageable)
Page<AuditLog> findByEntityType(String entityType, Pageable pageable)
Page<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId, Pageable pageable)
Page<AuditLog> findByIpAddress(String ipAddress, Pageable pageable)

// Date range queries
Page<AuditLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable)
Page<AuditLog> findByUserIdAndDateRange(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable)

// Entity history
List<AuditLog> findEntityHistory(String entityType, Long entityId, LocalDateTime start, LocalDateTime end)

// Recent activity
List<AuditLog> findRecentLogs(Pageable pageable)

// Lookup helpers
List<String> findAllActions()
List<String> findAllEntityTypes()
List<String> findAllIpAddresses()

// Statistics
List<Object[]> getActionStatistics()
List<Object[]> getEntityTypeStatistics()
List<Object[]> getDailyActivity(LocalDateTime daysAgo)

// Counts
long countByAction(String action)
long countByUserId(Long userId)
long countByEntityType(String entityType)
long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end)

// Cleanup
void deleteByCreatedAtBefore(LocalDateTime cutoffDate)
```

**Usage Example:**
```java
// Get entity history
List<AuditLog> history = auditLogRepository.findEntityHistory(
    "Cliente", clienteId, startDate, endDate
);

// Get daily activity statistics
LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
List<Object[]> activity = auditLogRepository.getDailyActivity(thirtyDaysAgo);

// Find recent logs
Page<AuditLog> recent = auditLogRepository.findRecentLogs(
    PageRequest.of(0, 10)
);
```

---

## Business Domain Repositories

### 6. ClienteRepository

**Location:** `com.financas.assembly.repository.ClienteRepository`

**Extends:** JpaRepository<Cliente, Long>

**Purpose:** Client management with polymorphic queries (PF/PJ).

**Key Methods:**

```java
// Basic finders
Optional<Cliente> findByEmail(String email)
List<Cliente> findByAtivoTrue()
List<Cliente> findByAtivoFalse()
List<Cliente> findByCidade(String cidade)
List<Cliente> findByEstado(String estado)

// Search
List<Cliente> findByNomeContainingIgnoreCase(String nome)

// Transaction-based queries
List<Cliente> findClientsWithTransactionsInPeriod(LocalDate start, LocalDate end)

// Counts
long countActiveClients()
boolean existsByEmail(String email)
```

**Usage Example:**
```java
// Find active clients
List<Cliente> activeClients = clienteRepository.findByAtivoTrue();

// Find clients with recent transactions
LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
List<Cliente> active = clienteRepository
    .findClientsWithTransactionsInPeriod(thirtyDaysAgo, LocalDate.now());
```

---

### 7. CategoriaRepository

**Location:** `com.financas.assembly.repository.CategoriaRepository`

**Extends:** JpaRepository<Categoria, Long>

**Purpose:** Category management with hierarchy support.

**Key Methods:**

```java
// Basic finders
Optional<Categoria> findByNome(String nome)
List<Categoria> findByAtivaTrue()
List<Categoria> findByTipo(Categoria.TipoCategoria tipo)
List<Categoria> findByTipoAndAtivaTrue(Categoria.TipoCategoria tipo)

// Hierarchy queries
List<Categoria> findRootCategories()  // No parent
List<Categoria> findByCategoriaPaiId(Long categoriaPaiId)

// Search
List<Categoria> findByNomeContainingIgnoreCase(String nome)

// Statistics
List<Object[]> findMostUsedCategories()
List<Object[]> findCategoriesWithTotalValue(TipoCategoria tipo, LocalDate start, LocalDate end)

// Counts
long countByTipo(Categoria.TipoCategoria tipo)
boolean existsByNome(String nome)
```

**Usage Example:**
```java
// Get root categories
List<Categoria> roots = categoriaRepository.findRootCategories();

// Get subcategories
List<Categoria> subcategories = categoriaRepository
    .findByCategoriaPaiId(parentId);

// Find income categories
List<Categoria> incomeCategories = categoriaRepository
    .findByTipoAndAtivaTrue(Categoria.TipoCategoria.RECEITA);

// Get most used categories
List<Object[]> mostUsed = categoriaRepository.findMostUsedCategories();
// Result: [Categoria, Long (transaction count)]
```

---

### 8. TransacaoRepository

**Location:** `com.financas.assembly.repository.TransacaoRepository`

**Extends:** JpaRepository<Transacao, Long>

**Purpose:** Transaction queries and financial calculations.

**Key Methods:**

#### Basic Queries
```java
List<Transacao> findByTipo(TipoTransacao tipo)
List<Transacao> findByStatus(StatusTransacao status)
List<Transacao> findByClienteId(Long clienteId)
List<Transacao> findByCategoriaId(Long categoriaId)
List<Transacao> findByRecorrenteTrue()
List<Transacao> findByDescricaoContainingIgnoreCase(String descricao)
```

#### Date Range Queries
```java
List<Transacao> findByDataTransacaoBetween(LocalDate start, LocalDate end)
Page<Transacao> findByDataTransacaoBetween(LocalDate start, LocalDate end, Pageable pageable)
List<Transacao> findByTipoAndDataTransacaoBetween(TipoTransacao tipo, LocalDate start, LocalDate end)
List<Transacao> findByClienteAndDateRange(Long clienteId, LocalDate start, LocalDate end)
```

#### Financial Calculations
```java
// Calculate total by type
BigDecimal calculateTotalByTypeAndDateRange(TipoTransacao tipo, LocalDate start, LocalDate end)

// Calculate balance (income - expenses)
BigDecimal calculateBalance(LocalDate start, LocalDate end)
```

#### Reporting Queries
```java
// Group by category
List<Object[]> findTotalsByCategory(TipoTransacao tipo, LocalDate start, LocalDate end)
// Result: [Categoria, BigDecimal (sum)]

// Group by payment method
List<Object[]> findStatsByPaymentMethod(LocalDate start, LocalDate end)
// Result: [MetodoPagamento, Long (count), BigDecimal (sum)]

// Daily summary
List<Object[]> findDailySummary(LocalDate start, LocalDate end)
// Result: [LocalDate, TipoTransacao, BigDecimal (sum)]

// Monthly summary
List<Object[]> findMonthlySummary()
// Result: [Year, Month, TipoTransacao, BigDecimal (sum)]

// Top expenses
List<Transacao> findTopExpenses(LocalDate start, LocalDate end, Pageable pageable)

// Overdue pending
List<Transacao> findOverduePendingTransactions()
```

**Usage Example:**
```java
// Calculate monthly balance
LocalDate firstDay = LocalDate.now().withDayOfMonth(1);
LocalDate lastDay = LocalDate.now();
BigDecimal balance = transacaoRepository.calculateBalance(firstDay, lastDay);

// Get expenses by category
List<Object[]> byCategory = transacaoRepository.findTotalsByCategory(
    Transacao.TipoTransacao.DESPESA,
    firstDay,
    lastDay
);

// Find top 10 expenses
List<Transacao> topExpenses = transacaoRepository.findTopExpenses(
    firstDay,
    lastDay,
    PageRequest.of(0, 10)
);

// Get monthly summary
List<Object[]> monthly = transacaoRepository.findMonthlySummary();
for (Object[] row : monthly) {
    Integer year = (Integer) row[0];
    Integer month = (Integer) row[1];
    TipoTransacao tipo = (TipoTransacao) row[2];
    BigDecimal total = (BigDecimal) row[3];
    // Process data...
}
```

---

## Common Patterns

### 1. Pagination Support

Many repositories support pagination for large datasets:

```java
// Example: Find transactions with pagination
Pageable pageable = PageRequest.of(0, 20, Sort.by("dataTransacao").descending());
Page<Transacao> transactions = transacaoRepository.findByDataTransacaoBetween(
    startDate, endDate, pageable
);

// Access page data
List<Transacao> content = transactions.getContent();
long totalElements = transactions.getTotalElements();
int totalPages = transactions.getTotalPages();
```

### 2. Custom Queries with @Query

Complex queries use JPQL:

```java
@Query("SELECT c, SUM(t.valor) FROM Cliente c " +
       "LEFT JOIN c.transacoes t " +
       "WHERE t.dataTransacao >= :date " +
       "GROUP BY c " +
       "ORDER BY SUM(t.valor) DESC")
List<Object[]> findTopClientsByRevenue(@Param("date") LocalDate date);
```

### 3. Modifying Queries

Update/delete operations require `@Modifying`:

```java
@Modifying
@Transactional
@Query("UPDATE User u SET u.active = false WHERE u.lastLogin < :date")
void deactivateInactiveUsers(@Param("date") LocalDateTime date);
```

### 4. Specification Support

UserRepository extends JpaSpecificationExecutor for dynamic queries:

```java
// Build dynamic criteria
Specification<User> spec = (root, query, cb) -> {
    List<Predicate> predicates = new ArrayList<>();

    if (active != null) {
        predicates.add(cb.equal(root.get("active"), active));
    }
    if (roleName != null) {
        predicates.add(cb.equal(root.join("roles").get("name"), roleName));
    }

    return cb.and(predicates.toArray(new Predicate[0]));
};

// Use specification
List<User> users = userRepository.findAll(spec);
```

---

## Repository Method Naming Conventions

Spring Data JPA derives queries from method names:

| Pattern | Example | SQL Equivalent |
|---------|---------|----------------|
| findBy{Property} | findByEmail | WHERE email = ? |
| findBy{Property}And{Property} | findByNomeAndAtivo | WHERE nome = ? AND ativo = ? |
| findBy{Property}Or{Property} | findByNomeOrEmail | WHERE nome = ? OR email = ? |
| findBy{Property}Containing | findByNomeContaining | WHERE nome LIKE %?% |
| findBy{Property}IgnoreCase | findByEmailIgnoreCase | WHERE LOWER(email) = LOWER(?) |
| findBy{Property}Between | findByDataBetween | WHERE data BETWEEN ? AND ? |
| findBy{Property}LessThan | findByValorLessThan | WHERE valor < ? |
| findBy{Property}GreaterThan | findByValorGreaterThan | WHERE valor > ? |
| findBy{Property}OrderBy{Property} | findByAtivoOrderByNome | WHERE ativo = ? ORDER BY nome |
| countBy{Property} | countByAtivo | SELECT COUNT(*) WHERE ativo = ? |
| existsBy{Property} | existsByEmail | SELECT COUNT(*) > 0 WHERE email = ? |
| deleteBy{Property} | deleteByAtivo | DELETE WHERE ativo = ? |

---

## Performance Considerations

### 1. Use Eager Fetching Wisely

```java
// Avoid N+1 queries
@Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
Optional<User> findByEmailWithRoles(@Param("email") String email);
```

### 2. Pagination for Large Results

```java
// Always use pagination for potentially large datasets
Page<Transacao> findByClienteId(Long clienteId, Pageable pageable);
```

### 3. Indexing

Ensure database columns used in WHERE clauses are indexed:
- Email fields (unique indexes)
- Foreign keys (automatically indexed)
- Date fields for range queries
- Status/type fields for filtering

### 4. Projection for Read-Only Queries

```java
// Use interface projections to fetch only needed fields
public interface ClienteSummary {
    Long getId();
    String getNome();
    String getEmail();
}

List<ClienteSummary> findBy();
```

---

## Next Steps

### Service Layer
Create service classes that use these repositories:
- UserService
- AuthService
- ClienteService
- TransacaoService
- CategoriaService

### Controller Layer
Create REST controllers:
- AuthController
- UserController
- ClienteController
- TransacaoController
- CategoriaController

---

## File Locations

All repositories are in:
```
backend/src/main/java/com/financas/assembly/repository/
├── UserRepository.java
├── RoleRepository.java
├── PermissionRepository.java
├── RefreshTokenRepository.java
├── AuditLogRepository.java
├── ClienteRepository.java
├── PessoaFisicaRepository.java
├── EmpresaRepository.java
├── CategoriaRepository.java
└── TransacaoRepository.java
```

---

**Status:** ✅ All repositories implemented and ready for use
**Last Updated:** 2025-10-16
**Total Methods:** 150+ query methods across all repositories
