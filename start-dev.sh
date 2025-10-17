#!/bin/bash

# Financial Assembly - Development Quick Start Script

set -e

echo "============================================"
echo "Financial Assembly - Development Setup"
echo "============================================"
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check prerequisites
echo "Checking prerequisites..."

# Check Docker
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Error: Docker is not installed${NC}"
    exit 1
fi

# Check Docker Compose
if ! command -v docker compose &> /dev/null; then
    echo -e "${RED}Error: Docker Compose is not installed${NC}"
    exit 1
fi

# Check Java
if ! command -v java &> /dev/null; then
    echo -e "${YELLOW}Warning: Java is not installed (required for local backend development)${NC}"
else
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 21 ]; then
        echo -e "${YELLOW}Warning: Java 21 or higher is recommended (current: $JAVA_VERSION)${NC}"
    else
        echo -e "${GREEN}Java $JAVA_VERSION found${NC}"
    fi
fi

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo -e "${YELLOW}Warning: Maven is not installed (required for local backend development)${NC}"
else
    echo -e "${GREEN}Maven found${NC}"
fi

# Check Node.js
if ! command -v node &> /dev/null; then
    echo -e "${YELLOW}Warning: Node.js is not installed (required for local frontend development)${NC}"
else
    NODE_VERSION=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
    if [ "$NODE_VERSION" -lt 20 ]; then
        echo -e "${YELLOW}Warning: Node.js 20 or higher is recommended (current: $NODE_VERSION)${NC}"
    else
        echo -e "${GREEN}Node.js $NODE_VERSION found${NC}"
    fi
fi

echo ""
echo "Select development mode:"
echo "1) Docker (Full stack with Docker Compose)"
echo "2) Local (PostgreSQL in Docker, Backend and Frontend locally)"
echo "3) Database only (PostgreSQL + PgAdmin in Docker)"
echo ""
read -p "Enter choice [1-3]: " choice

case $choice in
    1)
        echo ""
        echo -e "${GREEN}Starting full stack with Docker Compose...${NC}"
        docker compose up -d --build

        echo ""
        echo -e "${GREEN}Waiting for services to be ready...${NC}"
        sleep 10

        echo ""
        echo "============================================"
        echo -e "${GREEN}Services started successfully!${NC}"
        echo "============================================"
        echo ""
        echo "Frontend:  http://localhost"
        echo "Backend:   http://localhost:8080"
        echo "Swagger:   http://localhost:8080/api/v1/swagger-ui.html"
        echo "PgAdmin:   http://localhost:5050 (admin@financas.com / admin)"
        echo ""
        echo "To view logs: docker-compose logs -f"
        echo "To stop:      docker-compose down"
        ;;

    2)
        echo ""
        echo -e "${GREEN}Starting PostgreSQL and PgAdmin...${NC}"
        docker compose -f docker-compose.dev.yml up -d

        echo ""
        echo -e "${GREEN}Waiting for database to be ready...${NC}"
        sleep 5

        echo ""
        echo -e "${GREEN}Starting Backend...${NC}"
        cd backend
        mvn spring-boot:run -Dspring-boot.run.profiles=dev &
        BACKEND_PID=$!
        cd ..

        echo ""
        echo -e "${GREEN}Waiting for backend to start...${NC}"
        sleep 15

        echo ""
        echo -e "${GREEN}Starting Frontend...${NC}"
        cd frontend
        npm start &
        FRONTEND_PID=$!
        cd ..

        echo ""
        echo "============================================"
        echo -e "${GREEN}Services started successfully!${NC}"
        echo "============================================"
        echo ""
        echo "Frontend:  http://localhost:4200"
        echo "Backend:   http://localhost:8080"
        echo "Swagger:   http://localhost:8080/api/v1/swagger-ui.html"
        echo "PgAdmin:   http://localhost:5050 (admin@financas.com / admin)"
        echo ""
        echo "Backend PID:  $BACKEND_PID"
        echo "Frontend PID: $FRONTEND_PID"
        echo ""
        echo "To stop: kill $BACKEND_PID $FRONTEND_PID && docker-compose -f docker-compose.dev.yml down"
        ;;

    3)
        echo ""
        echo -e "${GREEN}Starting PostgreSQL and PgAdmin only...${NC}"
        docker compose -f docker-compose.dev.yml up -d

        echo ""
        echo -e "${GREEN}Waiting for database to be ready...${NC}"
        sleep 5

        echo ""
        echo "============================================"
        echo -e "${GREEN}Database services started successfully!${NC}"
        echo "============================================"
        echo ""
        echo "PostgreSQL: localhost:5432"
        echo "  Database: financas_db_dev"
        echo "  User:     financas_user"
        echo "  Password: financas_password"
        echo ""
        echo "PgAdmin:    http://localhost:5050"
        echo "  Email:    admin@financas.com"
        echo "  Password: admin"
        echo ""
        echo "To run backend: cd backend && mvn spring-boot:run"
        echo "To run frontend: cd frontend && npm start"
        echo ""
        echo "To stop: docker-compose -f docker-compose.dev.yml down"
        ;;

    *)
        echo -e "${RED}Invalid choice${NC}"
        exit 1
        ;;
esac

echo ""
echo -e "${GREEN}Setup complete!${NC}"
