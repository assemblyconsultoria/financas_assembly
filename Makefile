# Financial Assembly - Makefile
# Convenience commands for development

.PHONY: help install build test clean docker-up docker-down docker-build docker-clean run-backend run-frontend

# Default target
help:
	@echo "Financial Assembly - Available Commands"
	@echo ""
	@echo "Setup & Installation:"
	@echo "  make install          - Install all dependencies (backend + frontend)"
	@echo "  make install-backend  - Install backend dependencies"
	@echo "  make install-frontend - Install frontend dependencies"
	@echo ""
	@echo "Build:"
	@echo "  make build            - Build both backend and frontend"
	@echo "  make build-backend    - Build backend only"
	@echo "  make build-frontend   - Build frontend only"
	@echo ""
	@echo "Test:"
	@echo "  make test             - Run all tests"
	@echo "  make test-backend     - Run backend tests"
	@echo "  make test-frontend    - Run frontend tests"
	@echo "  make coverage         - Generate coverage reports"
	@echo ""
	@echo "Run:"
	@echo "  make run-backend      - Run backend (localhost:8080)"
	@echo "  make run-frontend     - Run frontend (localhost:4200)"
	@echo ""
	@echo "Docker:"
	@echo "  make docker-up        - Start all services with Docker Compose"
	@echo "  make docker-down      - Stop all services"
	@echo "  make docker-build     - Build Docker images"
	@echo "  make docker-clean     - Remove all containers and volumes"
	@echo "  make docker-dev       - Start development environment (DB only)"
	@echo ""
	@echo "Cleanup:"
	@echo "  make clean            - Clean build artifacts"
	@echo "  make clean-backend    - Clean backend build"
	@echo "  make clean-frontend   - Clean frontend build"
	@echo ""
	@echo "Code Quality:"
	@echo "  make lint             - Run linters"
	@echo "  make lint-frontend    - Run ESLint for frontend"
	@echo ""

# Installation
install: install-backend install-frontend

install-backend:
	@echo "Installing backend dependencies..."
	cd backend && mvn clean install -DskipTests

install-frontend:
	@echo "Installing frontend dependencies..."
	cd frontend && npm install

# Build
build: build-backend build-frontend

build-backend:
	@echo "Building backend..."
	cd backend && mvn clean package -DskipTests

build-frontend:
	@echo "Building frontend..."
	cd frontend && npm run build:prod

# Test
test: test-backend test-frontend

test-backend:
	@echo "Running backend tests..."
	cd backend && mvn test

test-frontend:
	@echo "Running frontend tests..."
	cd frontend && npm test

coverage:
	@echo "Generating coverage reports..."
	cd backend && mvn jacoco:report
	cd frontend && npm run test:coverage
	@echo "Backend coverage: backend/target/site/jacoco/index.html"
	@echo "Frontend coverage: frontend/coverage/financial-assembly-frontend/index.html"

# Run
run-backend:
	@echo "Starting backend on http://localhost:8080..."
	cd backend && mvn spring-boot:run

run-frontend:
	@echo "Starting frontend on http://localhost:4200..."
	cd frontend && npm start

# Docker
docker-up:
	@echo "Starting all services with Docker Compose..."
	docker-compose up -d
	@echo "Frontend: http://localhost"
	@echo "Backend: http://localhost:8080"
	@echo "Swagger: http://localhost:8080/api/v1/swagger-ui.html"

docker-down:
	@echo "Stopping all services..."
	docker-compose down

docker-build:
	@echo "Building Docker images..."
	docker-compose build

docker-clean:
	@echo "Removing all containers, volumes, and images..."
	docker-compose down -v --rmi all

docker-dev:
	@echo "Starting development environment (PostgreSQL + PgAdmin)..."
	docker-compose -f docker-compose.dev.yml up -d
	@echo "PostgreSQL: localhost:5432"
	@echo "PgAdmin: http://localhost:5050"

# Clean
clean: clean-backend clean-frontend

clean-backend:
	@echo "Cleaning backend build artifacts..."
	cd backend && mvn clean

clean-frontend:
	@echo "Cleaning frontend build artifacts..."
	cd frontend && rm -rf dist node_modules .angular

# Code Quality
lint: lint-frontend

lint-frontend:
	@echo "Running ESLint..."
	cd frontend && npm run lint

lint-fix:
	@echo "Running ESLint with auto-fix..."
	cd frontend && npm run lint:fix

# Database
db-migrate:
	@echo "Running database migrations..."
	cd backend && mvn flyway:migrate

db-clean:
	@echo "Cleaning database..."
	cd backend && mvn flyway:clean

# Logs
logs:
	@echo "Showing Docker logs..."
	docker-compose logs -f

logs-backend:
	@echo "Showing backend logs..."
	docker-compose logs -f backend

logs-frontend:
	@echo "Showing frontend logs..."
	docker-compose logs -f frontend

# Quick start
quick-start: docker-dev
	@echo "Waiting for database to be ready..."
	@sleep 5
	@echo "Starting backend..."
	@make run-backend &
	@echo "Starting frontend..."
	@make run-frontend
