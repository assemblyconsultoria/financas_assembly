# Frontend Authentication Implementation - Angular 18

## Overview
Complete JWT-based authentication system for the Angular 18 frontend, fully integrated with the Spring Boot backend.

## Architecture

### Authentication Flow
```
User Login → AuthService → Backend API → JWT Tokens Received
         ↓
   Store Tokens in localStorage
         ↓
   Update User State (BehaviorSubject + Signals)
         ↓
   AuthInterceptor adds token to all requests
         ↓
   AuthGuard protects routes
```

## Components Implemented

### 1. Core Models (`core/models/user.model.ts`)

```typescript
// User interface matching backend response
export interface User {
  id?: number;
  email: string;
  name: string;
  roles: string[];
}

// Login request
export interface LoginRequest {
  email: string;
  password: string;
}

// Registration request (no confirmPassword sent to backend)
export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

// Backend authentication response
export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;      // "Bearer"
  expiresIn: number;       // milliseconds
  email: string;
  name: string;
  roles: string[];
}

// Token refresh request
export interface RefreshTokenRequest {
  refreshToken: string;
}
```

### 2. AuthService (`core/services/auth.service.ts`)

**Purpose:** Centralized authentication logic and state management

**Key Features:**
- JWT token management (access + refresh tokens)
- User state management with RxJS BehaviorSubject
- Reactive authentication state with Angular Signals
- LocalStorage persistence
- Token validation and refresh

**Public API:**

```typescript
class AuthService {
  // Observable for reactive user state
  currentUser$: Observable<User | null>

  // Signal for authentication state
  isAuthenticated: Signal<boolean>

  // Authentication methods
  login(credentials: LoginRequest): Observable<AuthResponse>
  register(userData: RegisterRequest): Observable<AuthResponse>
  logout(): void
  refreshToken(): Observable<AuthResponse>

  // Utility methods
  getCurrentUser(): User | null
  getToken(): string | null
  getRefreshToken(): string | null
  isLoggedIn(): boolean
}
```

**Storage Keys:**
- `access_token` - JWT access token
- `refresh_token` - JWT refresh token
- `auth_user` - User object JSON

**Token Validation:**
- Automatic JWT token decoding
- Expiration checking
- Auto-logout on invalid/expired tokens

### 3. HTTP Interceptors

#### AuthInterceptor (`core/interceptors/auth.interceptor.ts`)

**Purpose:** Automatically inject JWT tokens into HTTP requests

**Features:**
- Adds `Authorization: Bearer {token}` header to all requests
- Handles 401 Unauthorized responses
- Auto-logout and redirect on authentication errors

```typescript
// Automatically applied to all HTTP requests
GET /api/pessoas-fisicas
Headers:
  Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

#### ErrorInterceptor (`core/interceptors/error.interceptor.ts`)

**Purpose:** Global HTTP error handling

**Features:**
- Consistent error message formatting
- Status code mapping
- Error logging
- User-friendly error messages

**Error Handling:**
- 400: Bad request
- 401: Unauthorized (triggers logout)
- 403: Forbidden
- 404: Not found
- 500: Internal server error

### 4. Route Guards

#### AuthGuard (`core/guards/auth.guard.ts`)

**Purpose:** Protect routes that require authentication

```typescript
{
  path: 'dashboard',
  canActivate: [authGuard],  // ← Requires authentication
  loadComponent: () => import('./dashboard.component')
}
```

**Features:**
- Checks if user is logged in
- Stores return URL for post-login redirect
- Redirects to login page if not authenticated

#### GuestGuard (`core/guards/auth.guard.ts`)

**Purpose:** Redirect authenticated users away from auth pages

```typescript
{
  path: 'auth/login',
  canActivate: [guestGuard],  // ← Redirects if already authenticated
  loadComponent: () => import('./login.component')
}
```

**Features:**
- Prevents authenticated users from accessing login/register pages
- Redirects to dashboard if already logged in

### 5. Authentication Components

#### LoginComponent (`features/auth/login/login.component.ts`)

**Features:**
- Reactive form with validation
- Email and password fields
- Password visibility toggle
- Loading state with spinner
- Error handling with snackbar notifications
- Remember return URL for redirect after login

**Validation:**
- Email: Required, valid email format
- Password: Required, minimum 6 characters

**User Experience:**
```
1. User enters email and password
2. Form validates input
3. Loading spinner shows during request
4. On success: Redirect to dashboard (or return URL)
5. On error: Show error message in snackbar
```

#### RegisterComponent (`features/auth/register/register.component.ts`)

**Features:**
- Reactive form with advanced validation
- Name, email, password, and confirm password fields
- Password strength validation
- Password match validation
- Password visibility toggles
- Loading state
- Error handling

**Validation:**
- Name: Required, minimum 3 characters
- Email: Required, valid email format
- Password: Required, minimum 6 characters, must contain:
  - Uppercase letter
  - Lowercase letter
  - Number
- Confirm Password: Must match password

**Smart Backend Integration:**
- Strips `confirmPassword` before sending to backend
- Backend only receives: `{ name, email, password }`

## Configuration

### Environment Configuration (`environments/environment.ts`)

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1',
  apiTimeout: 30000,
  enableDebugMode: true,
  version: '1.0.0'
};
```

**Production (`environment.prod.ts`):**
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://api.yourdomain.com/api/v1',
  apiTimeout: 30000,
  enableDebugMode: false,
  version: '1.0.0'
};
```

### Application Configuration (`app.config.ts`)

```typescript
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([
        authInterceptor,    // ← JWT token injection
        errorInterceptor    // ← Global error handling
      ])
    ),
    provideAnimations()
  ]
};
```

## Routing Configuration

### Main Routes (`app.routes.ts`)

```typescript
export const routes: Routes = [
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes')
  },
  {
    path: 'dashboard',
    loadChildren: () => import('./features/dashboard/dashboard.routes')
  },
  // Other routes...
];
```

### Auth Routes (`features/auth/auth.routes.ts`)

```typescript
export const AUTH_ROUTES: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./login/login.component'),
    canActivate: [guestGuard]  // ← Prevents access if already logged in
  },
  {
    path: 'register',
    loadComponent: () => import('./register/register.component'),
    canActivate: [guestGuard]
  }
];
```

### Protected Routes (Example)

```typescript
export const DASHBOARD_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./dashboard.component'),
    canActivate: [authGuard]  // ← Requires authentication
  }
];
```

## Usage Examples

### 1. Login

**Component:**
```typescript
// In any component
constructor(private authService: AuthService, private router: Router) {}

login() {
  const credentials = { email: 'user@example.com', password: 'password' };

  this.authService.login(credentials).subscribe({
    next: (response) => {
      console.log('Logged in:', response);
      this.router.navigate(['/dashboard']);
    },
    error: (error) => {
      console.error('Login failed:', error.message);
    }
  });
}
```

### 2. Register

```typescript
register() {
  const userData = {
    name: 'John Doe',
    email: 'john@example.com',
    password: 'SecurePass123'
  };

  this.authService.register(userData).subscribe({
    next: (response) => {
      console.log('Registered:', response);
      this.router.navigate(['/dashboard']);
    },
    error: (error) => {
      console.error('Registration failed:', error.message);
    }
  });
}
```

### 3. Logout

```typescript
logout() {
  this.authService.logout();
  // Automatically redirects to /auth/login
}
```

### 4. Get Current User

```typescript
// Option 1: Synchronous
const user = this.authService.getCurrentUser();
if (user) {
  console.log('Current user:', user.name, user.email);
}

// Option 2: Reactive (Observable)
this.authService.currentUser$.subscribe(user => {
  if (user) {
    console.log('User changed:', user);
  }
});

// Option 3: Reactive (Signal)
const isAuth = this.authService.isAuthenticated();
console.log('Is authenticated:', isAuth);
```

### 5. Check Authentication

```typescript
// In component
if (this.authService.isLoggedIn()) {
  // User is authenticated
}

// In template
@if (authService.isAuthenticated()) {
  <div>Welcome, {{ authService.getCurrentUser()?.name }}</div>
}
```

### 6. Manual HTTP Request with Token

```typescript
// Token is automatically added by AuthInterceptor
// No manual action needed!

this.http.get('/api/pessoas-fisicas').subscribe(response => {
  // Request automatically includes: Authorization: Bearer {token}
});
```

## State Management

### Reactive State with RxJS

```typescript
// Subscribe to user changes
this.authService.currentUser$.subscribe(user => {
  if (user) {
    console.log('User:', user.name, user.roles);
  } else {
    console.log('Not authenticated');
  }
});
```

### Reactive State with Signals (Angular 18+)

```typescript
// Use signal in component
const isAuthenticated = this.authService.isAuthenticated();

// Use in template
@if (authService.isAuthenticated()) {
  <div>Logged in content</div>
} @else {
  <div>Please log in</div>
}
```

## Security Features

### 1. Token Storage
- Access token: Short-lived (24 hours default)
- Refresh token: Long-lived (7 days default)
- Stored in localStorage (consider httpOnly cookies for production)

### 2. Token Refresh
```typescript
// Automatic refresh on 401 errors
// Manual refresh:
this.authService.refreshToken().subscribe({
  next: (response) => console.log('Token refreshed'),
  error: () => this.authService.logout()
});
```

### 3. Auto-Logout
- On token expiration
- On 401 Unauthorized responses
- On invalid token format

### 4. XSS Protection
- No inline script execution
- Token validation before use
- Secure token parsing

## Testing

### Unit Tests Example

```typescript
describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should login successfully', () => {
    const mockResponse: AuthResponse = {
      accessToken: 'mock-token',
      refreshToken: 'mock-refresh',
      tokenType: 'Bearer',
      expiresIn: 86400000,
      email: 'test@example.com',
      name: 'Test User',
      roles: ['ROLE_USER']
    };

    service.login({ email: 'test@example.com', password: 'password' })
      .subscribe(response => {
        expect(response).toEqual(mockResponse);
        expect(service.getToken()).toBe('mock-token');
      });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });
});
```

## Troubleshooting

### Common Issues

1. **CORS Errors**
   - Ensure backend CORS is configured for `http://localhost:4200`
   - Check `SecurityConfig.java` CORS configuration

2. **Token Not Being Sent**
   - Verify interceptors are registered in `app.config.ts`
   - Check token exists in localStorage
   - Ensure AuthService.getToken() returns valid token

3. **401 Errors After Login**
   - Check token format (should be JWT)
   - Verify backend JWT secret matches
   - Check token expiration

4. **Redirect Loop**
   - Ensure guestGuard is on auth routes only
   - Check authGuard is on protected routes
   - Verify token validation logic

## Production Checklist

- [ ] Update `environment.prod.ts` with production API URL
- [ ] Enable HTTPS for all requests
- [ ] Consider httpOnly cookies instead of localStorage
- [ ] Implement token refresh before expiration
- [ ] Add rate limiting on auth endpoints
- [ ] Enable security headers (CSP, HSTS)
- [ ] Implement session timeout warnings
- [ ] Add remember me functionality
- [ ] Implement two-factor authentication (2FA)
- [ ] Add password reset flow
- [ ] Enable email verification
- [ ] Add audit logging for auth events
- [ ] Implement brute force protection
- [ ] Add captcha on login/register
- [ ] Enable source maps only for staging

## Development Commands

```bash
# Install dependencies
npm install

# Start development server
npm start
# or
ng serve

# Build for production
npm run build:prod
# or
ng build --configuration production

# Run tests
npm test
# or
ng test

# Run linting
npm run lint
# or
ng lint

# Generate component
ng generate component features/auth/forgot-password
```

## Integration with Backend

### API Endpoints Used

- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/register` - User registration
- `POST /api/v1/auth/refresh` - Token refresh
- `POST /api/v1/auth/logout` - Logout (revoke refresh token)

### Request/Response Examples

**Login:**
```
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Response 200:
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "email": "user@example.com",
  "name": "John Doe",
  "roles": ["ROLE_USER"]
}
```

## Future Enhancements

1. **OAuth2 Integration**
   - Google Sign-In
   - GitHub Sign-In
   - Social login support

2. **Advanced Security**
   - Biometric authentication
   - Device fingerprinting
   - Suspicious activity detection

3. **User Experience**
   - Remember me functionality
   - Session management
   - Multi-device support
   - Login history

4. **Password Management**
   - Forgot password flow
   - Password strength meter
   - Password history
   - Password expiration

## Support

For issues or questions:
- Check browser console for errors
- Verify network requests in DevTools
- Review this documentation
- Check backend logs for API errors
