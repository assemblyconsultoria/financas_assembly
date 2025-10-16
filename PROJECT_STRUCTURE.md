# Financial Assembly - Project Structure

This document provides an overview of the complete project structure.

## Root Directory

```
financas_assembly/
├── backend/                      # Spring Boot Backend
├── frontend/                     # Angular Frontend
├── docker/                       # Docker configuration files
├── .github/                      # GitHub Actions workflows
├── docker-compose.yml            # Production Docker Compose
├── docker-compose.dev.yml        # Development Docker Compose
├── Jenkinsfile                   # Jenkins CI/CD pipeline
├── Makefile                      # Convenience commands
├── start-dev.sh                  # Quick start script
├── .gitignore                    # Git ignore rules
├── .editorconfig                 # Editor configuration
├── README.md                     # Main documentation
├── CONTRIBUTING.md               # Contribution guidelines
├── LICENSE                       # Apache 2.0 License
└── PROJECT_STRUCTURE.md          # This file
```

## Backend Structure (Spring Boot 3.x + Java 21)

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/financas/assembly/
│   │   │   ├── FinancialAssemblyApplication.java  # Main application class
│   │   │   ├── config/                            # Configuration classes
│   │   │   │   ├── CorsConfig.java
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   ├── SecurityConfig.java            # (to be created)
│   │   │   │   └── JwtConfig.java                 # (to be created)
│   │   │   ├── controller/                        # REST Controllers
│   │   │   │   ├── ClienteController.java         # (to be created)
│   │   │   │   ├── TransacaoController.java       # (to be created)
│   │   │   │   └── CategoriaController.java       # (to be created)
│   │   │   ├── dto/                               # Data Transfer Objects
│   │   │   │   ├── request/                       # Request DTOs
│   │   │   │   └── response/                      # Response DTOs
│   │   │   ├── entity/                            # JPA Entities
│   │   │   │   ├── BaseEntity.java
│   │   │   │   ├── Cliente.java                   # (to be created)
│   │   │   │   ├── PessoaFisica.java              # (to be created)
│   │   │   │   ├── Empresa.java                   # (to be created)
│   │   │   │   ├── Transacao.java                 # (to be created)
│   │   │   │   └── Categoria.java                 # (to be created)
│   │   │   ├── exception/                         # Exception handling
│   │   │   │   ├── GlobalExceptionHandler.java    # (to be created)
│   │   │   │   └── custom exceptions...           # (to be created)
│   │   │   ├── mapper/                            # MapStruct mappers
│   │   │   ├── repository/                        # Spring Data repositories
│   │   │   │   ├── ClienteRepository.java         # (to be created)
│   │   │   │   ├── TransacaoRepository.java       # (to be created)
│   │   │   │   └── CategoriaRepository.java       # (to be created)
│   │   │   ├── security/                          # Security components
│   │   │   │   ├── JwtTokenProvider.java          # (to be created)
│   │   │   │   ├── JwtAuthenticationFilter.java   # (to be created)
│   │   │   │   └── UserDetailsServiceImpl.java    # (to be created)
│   │   │   ├── service/                           # Business logic
│   │   │   │   ├── ClienteService.java            # (to be created)
│   │   │   │   ├── TransacaoService.java          # (to be created)
│   │   │   │   └── CategoriaService.java          # (to be created)
│   │   │   └── util/                              # Utility classes
│   │   │       ├── CpfValidator.java              # (to be created)
│   │   │       └── CnpjValidator.java             # (to be created)
│   │   └── resources/
│   │       ├── application.properties             # Main configuration
│   │       ├── application-dev.properties         # Development profile
│   │       ├── application-prod.properties        # Production profile
│   │       ├── application-test.properties        # Test profile
│   │       └── db/migration/                      # Flyway migrations
│   │           ├── V1__create_initial_schema.sql  # (to be created)
│   │           └── V2__insert_initial_data.sql    # (to be created)
│   └── test/
│       ├── java/com/financas/assembly/
│       │   ├── controller/                        # Controller tests
│       │   ├── service/                           # Service tests
│       │   ├── repository/                        # Repository tests
│       │   └── integration/                       # Integration tests
│       └── resources/
│           └── application-test.properties
├── pom.xml                                        # Maven configuration
├── Dockerfile                                      # Backend Docker image
└── .dockerignore                                   # Docker ignore rules
```

## Frontend Structure (Angular 18)

```
frontend/
├── src/
│   ├── app/
│   │   ├── app.component.ts                       # Root component
│   │   ├── app.component.html
│   │   ├── app.component.scss
│   │   ├── app.config.ts                          # Application configuration
│   │   ├── app.routes.ts                          # Route definitions
│   │   ├── core/                                  # Core module (singleton services)
│   │   │   ├── guards/                            # Route guards
│   │   │   │   └── auth.guard.ts                  # (to be created)
│   │   │   ├── interceptors/                      # HTTP interceptors
│   │   │   │   ├── auth.interceptor.ts            # (to be created)
│   │   │   │   └── error.interceptor.ts           # (to be created)
│   │   │   └── services/                          # Core services
│   │   │       ├── auth.service.ts                # (to be created)
│   │   │       └── storage.service.ts             # (to be created)
│   │   ├── shared/                                # Shared module (reusable)
│   │   │   ├── components/                        # Shared components
│   │   │   │   ├── loading/                       # Loading spinner
│   │   │   │   ├── confirmation-dialog/           # Confirmation dialog
│   │   │   │   └── error-message/                 # Error display
│   │   │   ├── directives/                        # Custom directives
│   │   │   │   └── cpf-cnpj-mask.directive.ts     # (to be created)
│   │   │   ├── pipes/                             # Custom pipes
│   │   │   │   ├── currency-brl.pipe.ts           # (to be created)
│   │   │   │   └── cpf-cnpj.pipe.ts               # (to be created)
│   │   │   └── models/                            # TypeScript interfaces
│   │   │       ├── cliente.model.ts               # (to be created)
│   │   │       ├── transacao.model.ts             # (to be created)
│   │   │       └── categoria.model.ts             # (to be created)
│   │   └── features/                              # Feature modules
│   │       ├── auth/                              # Authentication
│   │       │   ├── login/
│   │       │   ├── register/
│   │       │   └── auth.routes.ts                 # (to be created)
│   │       ├── dashboard/                         # Dashboard
│   │       │   ├── dashboard.component.ts         # (to be created)
│   │       │   └── dashboard.routes.ts            # (to be created)
│   │       ├── clientes/                          # Client management
│   │       │   ├── cliente-list/
│   │       │   ├── cliente-form/
│   │       │   ├── cliente-detail/
│   │       │   ├── clientes.service.ts            # (to be created)
│   │       │   └── clientes.routes.ts             # (to be created)
│   │       ├── transacoes/                        # Transaction management
│   │       │   ├── transacao-list/
│   │       │   ├── transacao-form/
│   │       │   ├── transacao-detail/
│   │       │   ├── transacoes.service.ts          # (to be created)
│   │       │   └── transacoes.routes.ts           # (to be created)
│   │       └── categorias/                        # Category management
│   │           ├── categoria-list/
│   │           ├── categoria-form/
│   │           ├── categorias.service.ts          # (to be created)
│   │           └── categorias.routes.ts           # (to be created)
│   ├── assets/                                    # Static assets
│   │   ├── images/                                # Images
│   │   └── i18n/                                  # Internationalization
│   │       ├── pt-BR.json                         # (to be created)
│   │       └── en-US.json                         # (to be created)
│   ├── environments/                              # Environment configs
│   │   ├── environment.ts                         # Development
│   │   └── environment.prod.ts                    # Production
│   ├── index.html                                 # Main HTML
│   ├── main.ts                                    # Application entry point
│   └── styles.scss                                # Global styles
├── angular.json                                    # Angular CLI configuration
├── package.json                                    # NPM dependencies
├── tsconfig.json                                   # TypeScript configuration
├── tsconfig.app.json                              # App TypeScript config
├── tsconfig.spec.json                             # Test TypeScript config
├── karma.conf.js                                  # Karma test configuration
├── proxy.conf.json                                # Development proxy
├── .eslintrc.json                                 # ESLint configuration
├── nginx.conf                                     # Nginx configuration
├── Dockerfile                                      # Frontend Docker image
└── .dockerignore                                   # Docker ignore rules
```

## Docker Configuration

```
docker/
└── postgres/
    └── init.sql                                    # Database initialization
```

## CI/CD Configuration

```
.github/
└── workflows/
    └── ci-cd.yml                                   # GitHub Actions pipeline

Jenkinsfile                                         # Jenkins pipeline
```

## Key Technologies

### Backend
- **Spring Boot 3.2.5** - Application framework
- **Java 21** - Programming language (latest LTS)
- **PostgreSQL 16** - Database
- **Flyway** - Database migrations
- **Spring Security + JWT** - Authentication/Authorization
- **MapStruct** - Object mapping
- **SpringDoc OpenAPI** - API documentation
- **JUnit 5 + Mockito** - Testing
- **TestContainers** - Integration testing
- **JaCoCo** - Code coverage

### Frontend
- **Angular 18** - Framework
- **TypeScript 5.4** - Language
- **Angular Material** - UI components
- **RxJS** - Reactive programming
- **ng2-charts** - Charts and visualizations
- **Jasmine + Karma** - Testing

### DevOps
- **Docker** - Containerization
- **Docker Compose** - Container orchestration
- **Nginx** - Web server (frontend)
- **Jenkins** - CI/CD
- **GitHub Actions** - CI/CD
- **SonarQube** - Code quality
- **OWASP Dependency Check** - Security scanning
- **Trivy** - Container scanning

## Next Steps

After the initial setup, the following components need to be implemented:

### Phase 1 - Core Entities
1. Entity classes (Cliente, PessoaFisica, Empresa, Transacao, Categoria)
2. Repository interfaces
3. Flyway migration scripts
4. Basic service layer

### Phase 2 - Backend API
1. DTOs and mappers
2. REST controllers
3. Exception handling
4. Security configuration (JWT)
5. Validation

### Phase 3 - Frontend Components
1. Authentication flow
2. Client management (CRUD)
3. Transaction management (CRUD)
4. Category management (CRUD)
5. Dashboard with charts

### Phase 4 - Advanced Features
1. Reporting
2. Filtering and search
3. Data export
4. Email notifications
5. Audit logging

### Phase 5 - Testing & Deployment
1. Unit tests (80%+ coverage)
2. Integration tests
3. E2E tests
4. Performance testing
5. Production deployment

## Development Workflow

1. Create feature branch from `develop`
2. Implement feature with tests
3. Run tests and linting
4. Create pull request
5. Code review
6. Merge to `develop`
7. Deploy to staging
8. Merge to `main` for production

## Quick Start Commands

```bash
# Start development environment (DB only)
./start-dev.sh

# Or use Makefile
make docker-dev        # Start PostgreSQL
make run-backend       # Start backend
make run-frontend      # Start frontend

# Full Docker stack
make docker-up

# Run tests
make test

# Clean build
make clean
```

For detailed instructions, see README.md
