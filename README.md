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
- Pricing Strategy for regular, premium, weekend multiplier, and discount-code pricing.
- Refund Strategy based on configurable refund policies.
- Payment Gateway abstraction with an in-memory implementation.
- Async notification event listener so booking confirmation/cancellation does not block the customer flow.
- Centralized validation and error responses.

## Assumptions

- Authentication is intentionally simple because advanced auth is out of scope. Requests identify a seeded user by headers.
- A seat can be held by only one active hold for a show. Expired holds are ignored and are cleaned up periodically.
- Confirmation must use active holds owned by the same customer and belonging to one show.
- Payment always succeeds in the in-memory gateway.
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
  -d '{"holdIds":[1,2],"discountCode":"WELCOME10"}'
```

Cancel booking:

```bash
curl -X POST http://localhost:8080/api/customer/bookings/1/cancel \
  -H 'X-User-Id: 2' \
  -H 'X-User-Role: CUSTOMER'
```

## Design Notes

- `BookingService` owns the transactional booking flow and locks selected seats before checking holds/bookings.
- `PricingPolicy` is a Strategy interface; `DefaultPricingPolicy` implements current pricing rules.
- `RefundCalculator` is a Strategy interface; `PolicyBasedRefundCalculator` reads the configured default policy.
- `PaymentGateway` isolates payment integration from booking orchestration.
- Domain events are published after confirmation/cancellation and handled asynchronously by `NotificationListener`.

## Tests

```bash
./mvnw test
```
