# Business rules

## Rental allocation

1. Only authenticated customers with a customer profile may create rentals.
2. Requested DVD copy IDs must be unique.
3. Copies are locked with `SELECT … FOR UPDATE` (`PESSIMISTIC_WRITE`).
4. Only `AVAILABLE` copies can be rented; status becomes `RENTED`.
5. Line charge = `daily_rate × rental_days` (request override or movie default), stored on the item.
6. Order starts as `ACTIVE` with `total_amount` = sum of line amounts.

## Returns

1. Only STAFF/ADMIN confirm returns.
2. Item must be `RENTED` or `OVERDUE`.
3. Overdue fee = `daily_rate × overdue_days × app.pricing.overdue-multiplier`, added to order total and stored on the item.
4. Copy transitions `RENTED` → `RETURNED_PENDING_INSPECTION` → inspection result (`AVAILABLE`, `DAMAGED`, `MAINTENANCE`, `LOST`).
5. When all items are `RETURNED`, order becomes `COMPLETED`.

## Inventory status machine

```
AVAILABLE → RENTED | MAINTENANCE | DAMAGED | LOST
RENTED → RETURNED_PENDING_INSPECTION | LOST
RETURNED_PENDING_INSPECTION → AVAILABLE | DAMAGED | MAINTENANCE | LOST
DAMAGED → MAINTENANCE | LOST | AVAILABLE
MAINTENANCE → AVAILABLE | DAMAGED | LOST
LOST → AVAILABLE
```

Invalid transitions are rejected with HTTP 409.

## Authorization

- Customers see only their own rentals.
- Staff manage inventory, returns, customers, reports.
- Admin-only: privileged admin routes (`/api/v1/admin/**`) and movie delete.

## Historical prices

Changing a movie’s `daily_rate` does not rewrite past `line_amount` / `overdue_amount` values.
