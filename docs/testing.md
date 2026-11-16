# Testing

## Unit tests

- `PricingServiceTest` — rental and overdue fee math, multiplier
- `DvdCopyStatusMachineTest` / `RentalOrderStatusMachineTest` — transition validation

Run: `cd backend && mvn -Dtest=PricingServiceTest,DvdCopyStatusMachineTest,RentalOrderStatusMachineTest test`

## Integration tests (Testcontainers MySQL 8.4)

Base class: `AbstractIntegrationTest` (`@Testcontainers(disabledWithoutDocker = true)`).

| Test | Covers |
|------|--------|
| `AuthIntegrationTest` | Register, login, `/me`, public catalog |
| `RentalFlowIntegrationTest` | Rent → staff return; ownership denial |
| `ConcurrentRentalIntegrationTest` | Two threads race one copy; exactly one succeeds |

Requires Docker access. If the daemon socket is not usable by the current user, these classes are skipped rather than failing.

## Frontend

`npm run build` typechecks and produces the production bundle (no separate Jest suite in MVP).

## CI

GitHub Actions (`.github/workflows/ci.yml`): backend `mvn test` + frontend `npm ci && npm run build` on Ubuntu (Docker available for Testcontainers).
