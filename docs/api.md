# API (`/api/v1`)

OpenAPI UI: `/api/v1/swagger-ui.html`  
Errors: RFC 9457 Problem Details (`application/problem+json`).

## Auth

| Method | Path | Access |
|--------|------|--------|
| GET | `/auth/csrf` | Public — obtain CSRF token |
| POST | `/auth/register` | Public — create CUSTOMER |
| POST | `/auth/login` | Public — session cookie |
| POST | `/auth/logout` | Authenticated |
| GET | `/auth/me` | Authenticated |

## Catalog

| Method | Path | Access |
|--------|------|--------|
| GET | `/movies` | Public — `q`, `genreId`, pageable |
| GET | `/movies/{id}` | Public |
| POST/PUT/DELETE | `/movies`… | STAFF/ADMIN (DELETE admin) |
| GET/POST | `/genres` | Public list; STAFF/ADMIN create |
| GET | `/movies/{id}/availability` | Public — AVAILABLE copies |

## Rentals

| Method | Path | Access |
|--------|------|--------|
| POST | `/rentals` | CUSTOMER |
| GET | `/rentals/me` | CUSTOMER |
| GET | `/rentals/{id}` | Owner or STAFF/ADMIN |
| GET | `/staff/rentals` | STAFF/ADMIN |
| POST | `/staff/returns` | STAFF/ADMIN |

## Inventory / customers / reports

| Method | Path | Access |
|--------|------|--------|
| GET/POST | `/staff/inventory` | STAFF/ADMIN |
| PUT | `/staff/inventory/{id}/status` | STAFF/ADMIN |
| GET/PUT | `/customers/me` | CUSTOMER |
| GET | `/staff/customers` | STAFF/ADMIN |
| GET | `/staff/reports/dashboard` | STAFF/ADMIN |
| GET | `/staff/reports/overdue` | STAFF/ADMIN |

Collection endpoints support Spring Data pagination (`page`, `size`, `sort`).
