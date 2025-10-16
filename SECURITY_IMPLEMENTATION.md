# Security Implementation Summary

## Overview
Complete JWT-based authentication and authorization system implemented for the Financial Assembly project.

## Components Implemented

### 1. Security Core Components

#### JwtTokenProvider (`security/JwtTokenProvider.java`)
- Generates and validates JWT access tokens
- Generates refresh tokens
- Extracts user information from tokens
- Token expiration management
- Uses HMAC-SHA256 algorithm with secure key

**Key Methods:**
- `generateToken(Authentication)` - Generate access token from authentication
- `generateTokenFromUsername(String)` - Generate token from username
- `generateRefreshToken(String)` - Generate refresh token
- `validateToken(String)` - Validate JWT token
- `getUsernameFromToken(String)` - Extract username from token

#### JwtAuthenticationFilter (`security/JwtAuthenticationFilter.java`)
- Intercepts all HTTP requests
- Extracts JWT token from Authorization header
- Validates token and sets authentication in SecurityContext
- Extends `OncePerRequestFilter` to ensure single execution per request

#### JwtAuthenticationEntryPoint (`security/JwtAuthenticationEntryPoint.java`)
- Handles authentication errors
- Returns JSON error responses with appropriate HTTP status codes
- Implements `AuthenticationEntryPoint`

#### UserDetailsServiceImpl (`security/UserDetailsServiceImpl.java`)
- Loads user details from database
- Implements Spring Security's `UserDetailsService`
- Used by authentication manager for login
- Methods:
  - `loadUserByUsername(String)` - Load user by email
  - `loadUserById(Long)` - Load user by ID (for token refresh)

#### SecurityConfig (`security/SecurityConfig.java`)
- Main security configuration class
- Configures HTTP security and authentication
- Defines public and protected endpoints
- CORS configuration
- Stateless session management (JWT-based)
- Password encoding (BCrypt)

**Public Endpoints:**
- `/api/auth/**` - Authentication endpoints (login, register, refresh)
- `/api/v1/swagger-ui/**` - API documentation
- `/api/v1/api-docs/**` - OpenAPI specs
- `/api/v1/actuator/health` - Health check
- OPTIONS requests (CORS preflight)

**Protected Endpoints:**
- All other endpoints require authentication

### 2. Authentication Service & Controllers

#### AuthService (`service/AuthService.java`)
Business logic for authentication operations:

**Methods:**
- `login(LoginRequest)` - Authenticate user and generate tokens
- `register(RegisterRequest)` - Register new user with default role
- `refreshToken(RefreshTokenRequest)` - Refresh access token using refresh token
- `logout(String)` - Revoke refresh token

**Features:**
- Password encoding with BCrypt
- Automatic role assignment (ROLE_USER for new users)
- Refresh token persistence in database
- Last login timestamp tracking
- Login attempt tracking and account lockout
- Email verification ready (currently auto-verified)

#### AuthController (`controller/AuthController.java`)
REST endpoints for authentication:

**Endpoints:**
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/refresh` - Refresh access token
- `POST /api/auth/logout` - User logout

All endpoints include validation and proper error handling.

### 3. DTOs (Data Transfer Objects)

Located in `dto/auth/`:

- **LoginRequest** - Email and password for login
- **RegisterRequest** - Name, email, and password for registration
- **AuthResponse** - JWT tokens, user info, and roles
- **RefreshTokenRequest** - Refresh token for token renewal

All DTOs include:
- Bean validation annotations
- Lombok annotations for boilerplate reduction
- Swagger documentation support

### 4. Exception Handling

#### GlobalExceptionHandler (`exception/GlobalExceptionHandler.java`)
Centralized exception handling for REST API:

**Handled Exceptions:**
- `MethodArgumentNotValidException` - Validation errors (400)
- `BadCredentialsException` - Invalid credentials (401)
- `UsernameNotFoundException` - User not found (404)
- `IllegalArgumentException` - Invalid arguments (400)
- `RuntimeException` - Runtime errors (400)
- `Exception` - All other exceptions (500)

**Response Format:**
```json
{
  "timestamp": "2025-10-16T18:00:00",
  "status": 401,
  "error": "Authentication Error",
  "message": "Email ou senha inválidos",
  "path": "/api/auth/login"
}
```

## Configuration

### Application Properties (`application.properties`)

```properties
# JWT Configuration
app.jwt.secret=your-secret-key-change-this-in-production-min-256-bits-required-for-hs256-algorithm-secure-key
app.jwt.expiration-ms=86400000           # 24 hours
app.jwt.refresh-expiration-ms=604800000  # 7 days
```

### Security Features

1. **Password Security**
   - BCrypt hashing algorithm
   - Configurable work factor (default: 10)

2. **Token Security**
   - HMAC-SHA256 signing algorithm
   - Configurable expiration times
   - Refresh token rotation supported
   - Token revocation via database

3. **Account Security**
   - Failed login attempt tracking
   - Automatic account lockout after 5 failed attempts (30 minutes)
   - Email verification ready (currently auto-verified)
   - Password reset token support (ready for implementation)

4. **CORS Security**
   - Configured allowed origins
   - Credentials support
   - Preflight caching

## Authentication Flow

### 1. Registration
```
POST /api/auth/register
{
  "name": "João Silva",
  "email": "joao@example.com",
  "password": "senha123"
}

Response:
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "email": "joao@example.com",
  "name": "João Silva",
  "roles": ["ROLE_USER"]
}
```

### 2. Login
```
POST /api/auth/login
{
  "email": "joao@example.com",
  "password": "senha123"
}

Response: Same as registration
```

### 3. Authenticated Request
```
GET /api/pessoas-fisicas
Headers:
  Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 4. Token Refresh
```
POST /api/auth/refresh
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}

Response:
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",  // New access token
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",  // Same refresh token
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "email": "joao@example.com",
  "name": "João Silva",
  "roles": ["ROLE_USER"]
}
```

### 5. Logout
```
POST /api/auth/logout
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}

Response:
{
  "message": "Logout realizado com sucesso"
}
```

## Database Schema

Authentication uses the following tables (already created via Flyway):

- **users** - User accounts with credentials
- **roles** - System roles (ROLE_USER, ROLE_ADMIN, etc.)
- **permissions** - Fine-grained permissions
- **user_roles** - Many-to-many: users ↔ roles
- **role_permissions** - Many-to-many: roles ↔ permissions
- **refresh_tokens** - Active refresh tokens
- **audit_logs** - Security audit trail

## Security Best Practices Implemented

✅ Stateless JWT authentication
✅ Secure password hashing (BCrypt)
✅ Token expiration and refresh mechanism
✅ Role-based access control (RBAC)
✅ Permission-based authorization ready
✅ Account lockout protection
✅ CORS configuration
✅ SQL injection protection (JPA/Hibernate)
✅ Input validation
✅ Centralized exception handling
✅ Audit logging ready
✅ Secure token signing (HMAC-SHA256)

## Next Steps

1. **Install JDK 21** - Required for compilation
2. **Start Database** - PostgreSQL with initial data
3. **Compile & Test** - Verify security implementation
4. **Frontend Integration** - Implement Angular authentication
5. **Additional Features**:
   - Email verification flow
   - Password reset flow
   - OAuth2/Social login
   - Two-factor authentication (2FA)
   - Rate limiting per endpoint
   - Advanced audit logging

## Testing the Security

### Using cURL:

```bash
# Register
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "password123"
  }'

# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'

# Access protected endpoint
curl -X GET http://localhost:8080/api/v1/pessoas-fisicas \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Using Swagger UI:

1. Navigate to: http://localhost:8080/api/v1/swagger-ui.html
2. Click "Authorize" button
3. Enter: `Bearer YOUR_ACCESS_TOKEN`
4. Test all endpoints with authentication

## Production Checklist

Before deploying to production:

- [ ] Change JWT secret to a strong random key (minimum 256 bits)
- [ ] Enable HTTPS/TLS
- [ ] Configure proper CORS allowed origins
- [ ] Enable email verification
- [ ] Implement password reset
- [ ] Set up rate limiting
- [ ] Configure secure session cookies
- [ ] Enable security headers (HSTS, CSP, etc.)
- [ ] Set up monitoring and alerting
- [ ] Review and test all security endpoints
- [ ] Perform security audit/penetration testing
- [ ] Configure firewall rules
- [ ] Set up database backups
- [ ] Implement proper logging and monitoring

## Support

For issues or questions:
- Check logs: `logging.level.com.financas.assembly=DEBUG`
- Review security logs: `logging.level.org.springframework.security=DEBUG`
- Test endpoints with Swagger UI
- Review this documentation
