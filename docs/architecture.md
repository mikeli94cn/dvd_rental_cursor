# Architecture

## Style

**Modular monolith** — one Spring Boot deployable, packages organized by feature:

| Module | Responsibility |
|--------|----------------|
| `common` | Security config, Problem Details errors, shared properties |
| `identity` | Users, roles, registration/login/logout/session |
| `customer` | Customer profiles linked 1:1 to users |
| `catalog` | Movies and genres |
| `inventory` | Physical DVD copies and status machine |
| `pricing` | Rental and overdue fee calculation |
| `rental` | Orders, items, returns |
| `reporting` | Dashboard and overdue reports |

Inside each module: `controller` → `service` → `repository` / `entity`, plus DTOs. Controllers never hold business rules; repositories never enforce domain transitions; JPA entities are never returned as API bodies.

## Runtime topology

```
Browser → Nginx (SPA + /api reverse proxy) → Spring Boot → MySQL
```

Same-origin in production (Nginx serves UI and proxies `/api`). Development uses Vite proxy to the backend.

## Auth

HTTP-only session cookie (`DVDSESSION`) with Spring Security. CSRF uses a readable cookie (`XSRF-TOKEN`) and `X-XSRF-TOKEN` header for state-changing requests. Passwords are BCrypt-hashed. Roles: `CUSTOMER`, `STAFF`, `ADMIN`.

## Layering reminders

- **Controller**: HTTP mapping, validation, authz annotations.
- **Service**: transactions, business rules, DTO mapping.
- **Repository**: persistence queries only.
- **Entity**: relational model + lifecycle timestamps.
- **DTO**: stable API contracts.

## Why not microservices?

The MVP has one team, one database, and strong transactional consistency needs (inventory allocation). A modular monolith keeps boundaries clear for later extraction without distributed complexity.
