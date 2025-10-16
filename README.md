# Financial Assembly - Sistema de Gestao Financeira

Sistema completo de gestao financeira para controle de fluxo de caixa de pessoas fisicas e empresas.

## Visao Geral

O Financial Assembly e uma aplicacao enterprise-grade desenvolvida com Spring Boot 3.x (Java 21) no backend e Angular 18 no frontend, projetada para gerenciar transacoes financeiras, categorias e clientes de forma segura e eficiente.

### Principais Funcionalidades

- Gestao de Clientes (Pessoas Fisicas e Empresas)
- Controle de Transacoes (Receitas e Despesas)
- Categorizacao de Transacoes
- Dashboard com Visualizacao de Fluxo de Caixa
- Relatorios Financeiros
- Autenticacao e Autorizacao com JWT
- Auditoria de Operacoes
- API RESTful documentada com OpenAPI/Swagger

## Arquitetura

```
financial-assembly/
├── backend/              # Spring Boot 3.x + Java 21
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/financas/assembly/
│   │   │   │   ├── config/         # Configuracoes
│   │   │   │   ├── controller/     # REST Controllers
│   │   │   │   ├── dto/            # Data Transfer Objects
│   │   │   │   ├── entity/         # Entidades JPA
│   │   │   │   ├── exception/      # Exception Handlers
│   │   │   │   ├── mapper/         # MapStruct Mappers
│   │   │   │   ├── repository/     # Spring Data Repositories
│   │   │   │   ├── security/       # Configuracoes de Seguranca
│   │   │   │   ├── service/        # Camada de Servico
│   │   │   │   └── util/           # Utilitarios
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── application-dev.properties
│   │   │       ├── application-prod.properties
│   │   │       └── db/migration/   # Scripts Flyway
│   │   └── test/                   # Testes
│   ├── pom.xml
│   └── Dockerfile
├── frontend/             # Angular 18
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/              # Servicos core, guards, interceptors
│   │   │   ├── shared/            # Componentes, pipes, diretivas compartilhadas
│   │   │   └── features/          # Modulos de funcionalidades
│   │   │       ├── auth/          # Autenticacao
│   │   │       ├── clientes/      # Gestao de Clientes
│   │   │       ├── transacoes/    # Gestao de Transacoes
│   │   │       ├── categorias/    # Gestao de Categorias
│   │   │       └── dashboard/     # Dashboard
│   │   ├── assets/
│   │   └── environments/
│   ├── package.json
│   ├── angular.json
│   └── Dockerfile
├── docker/               # Scripts e configuracoes Docker
├── .github/
│   └── workflows/        # GitHub Actions CI/CD
├── docker-compose.yml    # Docker Compose para producao
├── docker-compose.dev.yml # Docker Compose para desenvolvimento
└── Jenkinsfile           # Pipeline Jenkins

```

## Stack Tecnologico

### Backend
- **Framework:** Spring Boot 3.2.5
- **Linguagem:** Java 21
- **Build Tool:** Maven 3.9.6
- **Banco de Dados:** PostgreSQL 16
- **Migracao:** Flyway
- **Seguranca:** Spring Security 6 + JWT
- **Documentacao API:** SpringDoc OpenAPI 3.0
- **Mapeamento:** MapStruct 1.5.5
- **Testes:** JUnit 5, Mockito, TestContainers
- **Cobertura de Codigo:** JaCoCo

### Frontend
- **Framework:** Angular 18
- **Linguagem:** TypeScript 5.4
- **UI Components:** Angular Material
- **Graficos:** ng2-charts (Chart.js)
- **Validacao:** Reactive Forms + Custom Validators
- **Testes:** Jasmine, Karma, Cypress
- **Build:** Angular CLI

### DevOps
- **Containerizacao:** Docker
- **Orquestracao:** Docker Compose
- **CI/CD:** Jenkins, GitHub Actions
- **Qualidade de Codigo:** SonarQube
- **Seguranca:** OWASP Dependency Check, Trivy
- **Servidor Web:** Nginx (frontend)

## Pre-requisitos

### Desenvolvimento Local
- Java 21 ou superior
- Node.js 20 ou superior
- Maven 3.9.6 ou superior
- PostgreSQL 16 (ou Docker)
- Docker e Docker Compose (opcional)
- Git

### Desenvolvimento com Docker
- Docker 24.0 ou superior
- Docker Compose 2.20 ou superior

## Instalacao e Configuracao

### 1. Clone o Repositorio

```bash
git clone https://github.com/seu-usuario/financas_assembly.git
cd financas_assembly
```

### 2. Configuracao do Banco de Dados

#### Usando Docker (Recomendado para Desenvolvimento)

```bash
# Inicia apenas o PostgreSQL e PgAdmin
docker-compose -f docker-compose.dev.yml up -d
```

#### Instalacao Local do PostgreSQL

```bash
# Crie o banco de dados
psql -U postgres
CREATE DATABASE financas_db;
CREATE USER financas_user WITH PASSWORD 'financas_password';
GRANT ALL PRIVILEGES ON DATABASE financas_db TO financas_user;
```

### 3. Configuracao do Backend

```bash
cd backend

# Copie e configure o application.properties (se necessario)
cp src/main/resources/application.properties src/main/resources/application-local.properties

# Compile e execute os testes
mvn clean install

# Execute a aplicacao
mvn spring-boot:run

# OU com perfil especifico
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

O backend estara disponivel em: `http://localhost:8080`
Swagger UI: `http://localhost:8080/api/v1/swagger-ui.html`

### 4. Configuracao do Frontend

```bash
cd frontend

# Instale as dependencias
npm install

# Execute em modo de desenvolvimento
npm start

# OU compile para producao
npm run build:prod
```

O frontend estara disponivel em: `http://localhost:4200`

### 5. Execucao com Docker Compose (Aplicacao Completa)

```bash
# Build e inicia todos os servicos (PostgreSQL + Backend + Frontend)
docker-compose up -d --build

# Visualize os logs
docker-compose logs -f

# Pare os servicos
docker-compose down

# Pare e remova volumes
docker-compose down -v
```

Apos iniciar:
- Frontend: `http://localhost`
- Backend API: `http://localhost:8080`
- PgAdmin: `http://localhost:5050` (email: admin@financas.com, senha: admin)

## Testes

### Backend

```bash
cd backend

# Testes unitarios
mvn test

# Testes de integracao
mvn verify

# Relatorio de cobertura
mvn jacoco:report
# Abra: target/site/jacoco/index.html
```

### Frontend

```bash
cd frontend

# Testes unitarios
npm test

# Testes com cobertura
npm run test:coverage
# Abra: coverage/financial-assembly-frontend/index.html

# Testes E2E
npm run e2e
```

## Estrutura de Branches

- `main` - Branch de producao (protegida)
- `develop` - Branch de desenvolvimento (protegida)
- `feature/*` - Branches de funcionalidades
- `hotfix/*` - Branches de correcoes urgentes

## Convencoes de Commit

Siga a convencao Conventional Commits:

```
feat: adiciona nova funcionalidade de relatorios
fix: corrige calculo de saldo
docs: atualiza documentacao da API
test: adiciona testes para ClienteService
refactor: refatora validacao de CPF/CNPJ
chore: atualiza dependencias
```

## CI/CD Pipeline

### GitHub Actions

O pipeline e executado automaticamente em:
- Push para branches `main`, `develop`, `feature/*`
- Pull Requests para `main` e `develop`

Estagios:
1. Build Backend e Frontend
2. Testes Unitarios e de Integracao
3. Analise de Codigo (SonarQube)
4. Scan de Seguranca (OWASP, npm audit)
5. Build de Imagens Docker
6. Scan de Vulnerabilidades (Trivy)
7. Deploy para Staging/Production

### Jenkins

Configure o Jenkins com:
1. Maven 3.9.6
2. JDK 21
3. Node.js 20
4. Docker
5. SonarQube Scanner

Execute: `Jenkinsfile` na raiz do projeto

## Seguranca

### Implementado

- Autenticacao JWT com refresh tokens
- Senhas criptografadas com BCrypt (strength 12+)
- CORS configuravel
- Rate Limiting
- Validacao de entrada em todas as APIs
- Headers de seguranca HTTP
- Auditoria de operacoes
- HTTPS em producao
- Scans automaticos de vulnerabilidades

### Variaveis de Ambiente Sensiveis

Nunca commite credenciais. Use variaveis de ambiente:

```bash
# Backend
export DATABASE_URL=jdbc:postgresql://localhost:5432/financas_db
export DATABASE_USERNAME=financas_user
export DATABASE_PASSWORD=sua_senha_segura
export JWT_SECRET=sua_chave_jwt_segura_minimo_256_bits
```

## API Documentation

A documentacao completa da API esta disponivel via Swagger UI:

- Desenvolvimento: `http://localhost:8080/api/v1/swagger-ui.html`
- Producao: `https://api.financialassembly.com/api/v1/swagger-ui.html`

### Principais Endpoints

```
POST   /api/v1/auth/login           # Login
POST   /api/v1/auth/register        # Registro
POST   /api/v1/auth/refresh         # Refresh Token

GET    /api/v1/clientes             # Listar clientes
POST   /api/v1/clientes             # Criar cliente
GET    /api/v1/clientes/{id}        # Buscar cliente
PUT    /api/v1/clientes/{id}        # Atualizar cliente
DELETE /api/v1/clientes/{id}        # Deletar cliente

GET    /api/v1/transacoes           # Listar transacoes
POST   /api/v1/transacoes           # Criar transacao
GET    /api/v1/transacoes/{id}      # Buscar transacao
PUT    /api/v1/transacoes/{id}      # Atualizar transacao
DELETE /api/v1/transacoes/{id}      # Deletar transacao

GET    /api/v1/categorias           # Listar categorias
POST   /api/v1/categorias           # Criar categoria
```

## Monitoramento

### Actuator Endpoints

```
GET /api/v1/actuator/health         # Health check
GET /api/v1/actuator/metrics        # Metricas
GET /api/v1/actuator/info           # Informacoes da aplicacao
```

## Troubleshooting

### Backend nao inicia

1. Verifique se o PostgreSQL esta rodando
2. Verifique as credenciais do banco de dados
3. Verifique se a porta 8080 esta disponivel

### Frontend nao compila

1. Delete `node_modules` e `package-lock.json`
2. Execute `npm install` novamente
3. Verifique a versao do Node.js (>= 20)

### Docker Compose falha

1. Verifique se as portas nao estao em uso
2. Execute `docker-compose down -v` para limpar volumes
3. Reconstrua as imagens: `docker-compose build --no-cache`

## Contribuindo

1. Fork o repositorio
2. Crie uma branch de feature (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudancas (`git commit -m 'feat: adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## Licenca

Este projeto esta sob a licenca Apache 2.0. Veja o arquivo LICENSE para mais detalhes.

## Contato

Financial Assembly Team - support@financialassembly.com

Link do Projeto: https://github.com/seu-usuario/financas_assembly

---

Desenvolvido com Spring Boot 3.x, Angular 18 e muito cafe.
