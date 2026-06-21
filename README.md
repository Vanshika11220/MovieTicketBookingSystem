# Movie Ticket Booking System

Spring Boot implementation of the take-home assignment in `Movie Ticket Booking System.pdf`.

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Web, Spring Data JPA, Bean Validation
- H2 in-memory database
- Maven

## Implemented Scope

- Admin APIs to manage cities, theaters, seat layouts, movies, shows, discount codes, and refund policies.
- Customer APIs to browse shows, inspect seat availability, hold seats, confirm bookings, cancel bookings, and view booking history.
- H2 persistence with seeded demo data.
- Basic role-based access control using `X-User-Id` and `X-User-Role` headers.
- Seat-level concurrency control using pessimistic row locks on selected seats.
- Time-bound seat holds with scheduled expiry and expiry checks during booking.
- Pricing Strategy for regular seats, premium seats, weekend pricing, and discount-code pricing.
- Refund Strategy based on configurable refund policies.
- Payment Strategy implementations for `CARD`, `UPI`, and `WALLET`.
- Observer Pattern for async email/SMS booking notifications.
- Centralized validation and error responses.

## Assumptions

- Authentication is intentionally simple because advanced auth is out of scope. Requests identify a seeded user by headers.
- A seat can be held by only one active hold for a show. Expired holds are ignored and are cleaned up periodically.
- Confirmation must use active holds owned by the same customer and belonging to one show.
- Payment always succeeds via the selected {@code PaymentStrategy} implementation.
- The active default refund policy applies to all shows.
- Weekend pricing is applied on Saturday and Sunday.

## Seeded Users

| ID | Email | Role |
| --- | --- | --- |
| 1 | admin@example.com | ADMIN |
| 2 | customer@example.com | CUSTOMER |
| 3 | customer2@example.com | CUSTOMER |

## Run

```bash
./mvnw spring-boot:run
```

H2 console: `http://localhost:8080/h2-console`

JDBC URL: `jdbc:h2:mem:movietickets`

## Example Flow

Browse shows:

```bash
curl http://localhost:8080/api/shows
```

View seats:

```bash
curl http://localhost:8080/api/shows/1/seats
```

Hold seats:

```bash
curl -X POST http://localhost:8080/api/customer/holds \
  -H 'Content-Type: application/json' \
  -H 'X-User-Id: 2' \
  -H 'X-User-Role: CUSTOMER' \
  -d '{"showId":1,"seatIds":[1,2]}'
```

Confirm booking:

```bash
curl -X POST http://localhost:8080/api/customer/bookings \
  -H 'Content-Type: application/json' \
  -H 'X-User-Id: 2' \
  -H 'X-User-Role: CUSTOMER' \
  -d '{"holdIds":[1,2],"discountCode":"WELCOME10","paymentMethod":"CARD"}'
```

Cancel booking:

```bash
curl -X POST http://localhost:8080/api/customer/bookings/1/cancel \
  -H 'X-User-Id: 2' \
  -H 'X-User-Role: CUSTOMER'
```

## Design Notes

### Package layout

- `domain/` — JPA entities
- `domain/enums/` — shared enums (`BookingStatus`, `HoldStatus`, `PaymentMethod`, `PaymentStatus`, `Role`, `SeatType`, `NotificationType`)
- `booking/` — customer booking flow split into focused services
- `pricing/` — Strategy pattern for seat pricing and modifiers
- `payment/` — Strategy pattern for payment methods
- `refund/` — Strategy pattern for refund calculation
- `notification/` — Observer pattern for async email/SMS notifications

### Design patterns

- **Strategy** — `SeatPricingStrategy`, `PricingModifier`, `PaymentStrategy`, `RefundCalculator`
- **Observer** — `BookingNotificationSubject` notifies `NotificationObserver` implementations
- **Facade** — `BookingService` delegates to specialized booking services
- **Repository** — Spring Data JPA for persistence

### Booking services

- `SeatAvailabilityService` — pessimistic seat locking and availability checks
- `SeatHoldService` — time-bound holds and scheduled expiry
- `BookingConfirmationService` — pricing, payment, and confirmation
- `BookingCancellationService` — refund calculation and cancellation
- `BookingQueryService` — booking history
- `BookingService` — thin facade used by the controller

## Tests

```bash
./mvnw test
```
