# DVD Rental System

Full-stack physical DVD rental website: Spring Boot 3.5 modular monolith, React + Ant Design SPA, MySQL 8.4, Flyway, Docker Compose.

Customers browse the catalog, rent available discs for pickup at the store, and track rentals. Staff manage inventory, process returns with inspection, and view overdue reports.

## Quick start (Docker Compose)

Requires Docker with permission to use the daemon (user in the `docker` group).

```bash
cp .env.example .env
docker compose up --build
```

Open http://localhost (or `APP_PORT` from `.env`).

### Seeded accounts (when `APP_SEED_ENABLED=true`)

| Email | Password | Roles |
|-------|----------|-------|
| admin@dvdrental.local | Admin123! | ADMIN, STAFF |
| staff@dvdrental.local | Staff123! | STAFF |
| customer@dvdrental.local | Customer123! | CUSTOMER |

## Local development

### Backend

```bash
# Start MySQL 8.4 with user/password/db matching application.yml (dvd/dvd/dvd_rental)
cd backend
mvn spring-boot:run
```

API: http://localhost:8080  
Swagger UI: http://localhost:8080/api/v1/swagger-ui.html  
Health: http://localhost:8080/actuator/health

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Vite proxies `/api` to `http://localhost:8080`. Open http://localhost:5173.

## Tests

```bash
cd backend && mvn test
cd frontend && npm run build
```

Backend unit tests always run. Integration tests use Testcontainers MySQL and require a working Docker environment (`docker` group). Without Docker they are skipped (`disabledWithoutDocker`).

## Project layout

```
backend/     Spring Boot API (package-by-feature)
frontend/    React + TypeScript + Vite + Ant Design
docker/      Dockerfiles + Nginx config
docs/        Architecture, database, API, business rules, deployment, testing, learning guide
compose.yaml Production-style local stack
```

## Documentation

- [Architecture](docs/architecture.md)
- [Database](docs/database.md)
- [API](docs/api.md)
- [Business rules](docs/business-rules.md)
- [Deployment](docs/deployment.md)
- [Testing](docs/testing.md)
- [Learning guide](docs/learning-guide.md)
- [Project spec](doc/PROJECT_SPEC.md)

## Verification checklist

1. Register / login as customer (session cookie `DVDSESSION`).
2. Admin creates movies and DVD copies (or use seed data).
3. Customer rents an available copy; cart → confirm.
4. Concurrent rent of the same copy: only one succeeds (integration test).
5. Customer sees own rentals only.
6. Staff confirms return with inspection → copy becomes AVAILABLE / DAMAGED / etc.
7. Overdue report lists late items; overdue fees apply on return.
8. `mvn test` and `npm run build` succeed.
9. `docker compose up --build` starts the stack (when Docker is available).
