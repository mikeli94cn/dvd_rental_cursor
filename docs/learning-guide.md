# Learning guide

How this project maps to professional Java backend practice and common interview topics.

## Modular monolith vs microservices

We kept one deployable with package boundaries. Interview angle: when consistency and operational simplicity beat network boundaries; how to extract a module later (inventory or pricing) once team/scale demands it.

## Transactions and locking

`RentalService.createRental` is `@Transactional` and uses pessimistic locks so two customers cannot rent the same physical disc. Interview angle: optimistic vs pessimistic locking, isolation levels, why inventory allocation is a classic race.

## Never expose entities

API responses are DTOs. Interview angle: lazy-loading surprises, accidental over-fetching, API stability when the schema evolves.

## Security layering

UI route guards are UX only; Spring Security + service ownership checks are authoritative. Interview angle: CSRF with cookie sessions for SPAs, why not put JWTs in `localStorage`, BCrypt password storage.

## Flyway over `ddl-auto=update`

Schema is versioned and reviewed. Interview angle: migration discipline in production, expand/contract patterns, why Hibernate validate mode pairs with Flyway.

## Money as `BigDecimal` / `DECIMAL`

Floating point is wrong for currency. Interview angle: scale/rounding modes, storing historical charges separately from current price lists.

## Testcontainers

Integration tests hit real MySQL locking semantics. Interview angle: why mocking the database is insufficient for concurrency tests.

## Suggested study path

1. Trace a rental from `RentalController` through lock → pricing → status machines.
2. Break the concurrent test intentionally (remove `@Lock`) and observe double allocation.
3. Add a Flyway `V2` migration (e.g. movie rating) without changing Hibernate entity first — see validate fail — then update the entity.
