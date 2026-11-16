# Deployment

## Docker Compose

```bash
cp .env.example .env
docker compose up --build -d
```

Services:

| Service | Role | Exposure |
|---------|------|----------|
| `mysql` | MySQL 8.4 + volume `mysql_data` | Internal only |
| `backend` | Spring Boot API + Flyway on startup | Internal |
| `frontend` | Nginx SPA + `/api` proxy | Host `APP_PORT` (default 80) |

Health checks: MySQL `mysqladmin ping`; backend `/actuator/health`; frontend waits for backend healthy.

## Environment

See `.env.example`. Important variables:

- `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` — backend JDBC
- `APP_SEED_ENABLED` — set `false` in production
- `SESSION_COOKIE_SECURE` — `true` behind HTTPS
- `CORS_ALLOWED_ORIGINS` — only needed for cross-origin Vite/dev; same-origin Compose needs little CORS

Production profile: `SPRING_PROFILES_ACTIVE=prod` disables seeding and tightens actuator exposure.

## Nginx

`docker/nginx.conf` serves the SPA (`try_files`) and reverse-proxies `/api/` to `backend:8080`, forwarding cookies for session auth.

## Memory

Single JVM + Nginx + MySQL is suitable for a small Linux VPS (1–2 GB RAM recommended for MySQL comfort).
