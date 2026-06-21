# Movie Ticket Booking System - Project Details And Flows

## 1. Project Summary

This project is a Spring Boot backend for a movie ticket booking system. It supports multiple cities, theaters, shows, seat layouts, seat-level holds, booking confirmation, cancellation, refunds, pricing rules, discount codes, role-based access control, and asynchronous notifications.

The implementation is intentionally scoped as a clean monolith because the assignment asks for Spring Boot, REST APIs, persistence, validation, error handling, role-based access control, tests, and maintainable code, while explicitly excluding UI, deployment, advanced auth, and distributed systems.

## 2. Tech Stack

- Java 17
- Spring Boot 3.5
- Spring Web
- Spring Data JPA
- Hibernate
- H2 in-memory database
- Bean Validation
- Maven
- JUnit and MockMvc for integration tests

## 3. Main Modules

- `admin`: Admin-facing APIs and service methods for catalog setup.
- `catalog`: Public browsing APIs for shows and seat availability.
- `booking`: Customer booking lifecycle: hold, confirm, cancel, history.
- `domain`: JPA entities and enums.
- `repository`: Spring Data JPA repositories.
- `pricing`: Pricing Strategy abstractions for seat pricing and ordered pricing modifiers.
- `refund`: Refund Strategy abstraction and policy-based implementation.
- `payment`: Payment Strategy abstraction with card, UPI, and wallet implementations.
- `notification`: Observer Pattern for async booking confirmation/cancellation notifications.
- `security`: Lightweight role enforcement using request headers.
- `common`: Shared API exceptions and centralized error responses.
- `config`: Web configuration and seeded demo data.

## 4. Domain Model Understanding

The system models these primary concepts:

- `UserAccount`: A user with either `ADMIN` or `CUSTOMER` role.
- `City`: A city where theaters exist.
- `Theater`: A physical theater in a city.
- `Seat`: A theater seat with row, number, and type: `REGULAR` or `PREMIUM`.
- `Movie`: Movie metadata such as title, duration, and language.
- `MovieShow`: A scheduled movie screening at a theater with pricing configuration.
- `SeatHold`: A temporary, time-bound reservation for a seat before payment confirmation.
- `Booking`: A confirmed purchase containing seats, amount, status, and refund details.
- `Payment`: Payment record created through the payment gateway.
- `DiscountCode`: Percentage discount code with active and expiry controls.
- `RefundPolicy`: Configurable cancellation policy.

## 5. Role-Based Access Control

Advanced authentication is out of scope, so the project uses simple headers:

- `X-User-Id`
- `X-User-Role`

Admin endpoints require:

```text
X-User-Role: ADMIN
```

Customer endpoints require:

```text
X-User-Role: CUSTOMER
```

Seeded users:

| ID | Email | Role |
| --- | --- | --- |
| 1 | admin@example.com | ADMIN |
| 2 | customer@example.com | CUSTOMER |
| 3 | customer2@example.com | CUSTOMER |

## 6. API List

### Catalog APIs

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/shows` | Browse upcoming shows. Optional `cityId` query parameter. |
| GET | `/api/shows/{showId}/seats` | View all seats for a show and their availability. |

### Admin APIs

All admin APIs require `X-User-Id` and `X-User-Role: ADMIN`.

| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/api/admin/cities` | Create city. |
| POST | `/api/admin/theaters` | Create theater under a city. |
| POST | `/api/admin/seat-layouts` | Create seats for a theater. |
| POST | `/api/admin/movies` | Create movie. |
| POST | `/api/admin/shows` | Create show with pricing and hold configuration. |
| POST | `/api/admin/discounts` | Create discount code. |
| POST | `/api/admin/refund-policies` | Create refund policy. |

### Customer APIs

All customer APIs require `X-User-Id` and `X-User-Role: CUSTOMER`.

| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/api/customer/holds` | Temporarily hold seats for a show. |
| POST | `/api/customer/bookings` | Confirm booking from active holds. |
| GET | `/api/customer/bookings` | View customer booking history. |
| POST | `/api/customer/bookings/{bookingId}/cancel` | Cancel booking and calculate refund. |

## 7. Whole Booking Flow

### Admin Setup Flow

1. Admin creates a city.
2. Admin creates a theater in that city.
3. Admin creates the seat layout for that theater.
4. Admin creates a movie.
5. Admin creates a show for that movie and theater.
6. Admin creates a discount code.
7. Admin creates a refund policy.

### Customer Booking Flow

1. Customer browses shows using `/api/shows`.
2. Customer checks seat availability using `/api/shows/{showId}/seats`.
3. Customer holds selected seats using `/api/customer/holds`.
4. System locks selected seats transactionally and checks:
   - Seats exist.
   - Seats belong to the show theater.
   - Seats are not already confirmed in another booking.
   - Seats are not actively held by another user.
5. System creates active `SeatHold` rows with an expiry timestamp.
6. Customer confirms booking using `/api/customer/bookings`.
7. System validates:
   - All hold IDs exist.
   - Holds are still active.
   - Holds belong to the same customer.
   - Holds belong to the same show.
   - Holds are not expired.
   - Seats are not already booked.
8. System calculates price using `PricingPolicy`, seat pricing strategies, and pricing modifiers.
9. System charges payment through the selected `PaymentStrategy`.
10. System creates a confirmed `Booking`.
11. System marks holds as confirmed.
12. System asks `BookingNotificationSubject` to publish a booking confirmation notification.
13. Async observer fan-out notifies email and SMS observers.

### Cancellation And Refund Flow

1. Customer calls `/api/customer/bookings/{bookingId}/cancel`.
2. System verifies the booking belongs to the customer.
3. System verifies the booking is not already cancelled.
4. `RefundCalculator` loads the active default refund policy.
5. System checks minutes remaining before show start.
6. If cancellation is allowed by policy, refund amount is calculated.
7. Booking status becomes `CANCELLED`.
8. Payment status becomes `REFUNDED`.
9. System asks `BookingNotificationSubject` to publish a booking cancellation notification.
10. Async observer fan-out notifies email and SMS observers.

## 8. Pricing Understanding

Pricing is implemented through explicit Strategy interfaces.

Current pricing strategies:

- `RegularSeatPricingStrategy`: returns show `basePrice`.
- `PremiumSeatPricingStrategy`: returns show `basePrice + premiumSurcharge`.
- `WeekendPricingModifier`: applies `weekendMultiplier` for Saturday and Sunday shows.
- `DiscountPricingModifier`: applies active non-expired discount code percentage.
- Final amount is rounded to 2 decimals.

This keeps the pricing logic replaceable without changing the booking orchestration code.

## 9A. Payment Understanding

Payments are implemented through the `PaymentStrategy` interface.

Current payment strategies:

- `CardPaymentStrategy`
- `UpiPaymentStrategy`
- `WalletPaymentStrategy`

`PaymentProcessor` selects the correct strategy using the `paymentMethod` supplied in the booking confirmation request. If the request does not include `paymentMethod`, the system defaults to `CARD`.

## 9. Refund Understanding

Refunds are implemented through the `RefundCalculator` Strategy interface.

Current default refund logic:

- Load the active default `RefundPolicy`.
- Compare current time with show start time.
- If cancellation happens before the policy cutoff, calculate refund as:

```text
amountPaid * refundPercentage / 100
```

- Otherwise refund is zero.

## 10. Notification Understanding

Notifications are implemented with the Observer Pattern.

- `BookingNotificationSubject` is the subject.
- `NotificationObserver` is the observer interface.
- `EmailNotificationObserver` and `SmsNotificationObserver` are concrete observers.
- The subject notifies observers asynchronously so booking confirmation/cancellation does not block on notification delivery.

## 11. Concurrency Handling

The most important race condition is two users trying to book the same seat at the same time.

The system handles this by:

- Sorting selected seat IDs.
- Fetching selected seats using a pessimistic write lock.
- Checking confirmed bookings while the transaction holds the lock.
- Checking active non-expired holds while the transaction holds the lock.
- Creating holds or bookings inside the same transaction.

This serializes competing booking attempts for the same seats and prevents double allocation.

## 12. Seat Hold Expiry

Seat holds are time-bound.

Expiry is handled in two ways:

- Scheduled cleanup marks old active holds as `EXPIRED`.
- Booking operations call expiry cleanup before checking or confirming holds.

This means expired holds do not block new customers even if the scheduled task has not run yet.

## 13. Error Handling And Validation

Validation is implemented using Bean Validation annotations on request DTOs.

Centralized error handling returns a consistent `ApiError` response with:

- timestamp
- HTTP status
- error label
- details

Business errors use `ApiException`, for example:

- `404 NOT_FOUND` for missing resources.
- `400 BAD_REQUEST` for invalid business input.
- `401 UNAUTHORIZED` for missing role headers.
- `403 FORBIDDEN` for wrong role or wrong customer ownership.
- `409 CONFLICT` for held/booked seats, expired holds, or already-cancelled bookings.

## 14. Seeded Demo Data

On application startup, the app seeds:

- 1 admin user
- 2 customer users
- 1 city
- 1 theater
- 24 seats
- 1 movie
- 1 upcoming show
- `WELCOME10` discount code
- full-refund default refund policy

This makes the APIs usable immediately after `./mvnw spring-boot:run`.

## 15. Postman Collection

The collection is available at:

```text
postman/MovieTicketBookingSystem.postman_collection.json
```

It contains:

- `Whole Flow APIs - Run In Order`
- `Catalog APIs`
- `Admin APIs`
- `Customer APIs`
- `Negative / Validation Examples`

Run the whole-flow folder in order after starting the app.

## 16. Running The Project

```bash
./mvnw spring-boot:run
```

Base URL:

```text
http://localhost:8080
```

H2 console:

```text
http://localhost:8080/h2-console
```

JDBC URL:

```text
jdbc:h2:mem:movietickets
```

## 17. Running Tests

```bash
./mvnw test
```

Current test coverage includes:

- Spring context startup.
- Customer hold, confirm, cancel, and booking history flow.
- Double-hold conflict for another customer.
- Role-based denial for customer trying to call admin API.

## 18. Design Patterns Used

- Strategy Pattern: `SeatPricingStrategy`, `PricingModifier`, `PaymentStrategy`, `RefundCalculator`.
- Observer Pattern: `BookingNotificationSubject`, `NotificationObserver`.
- Repository Pattern: Spring Data repositories.
- DTO Pattern: request and response contracts separate from entities.
- Service Layer Pattern: transaction and business rules are kept out of controllers.

## 19. Important Assumptions

- UI/frontend is out of scope.
- OAuth, SSO, MFA, and production auth are out of scope.
- Payment strategies are simulated and always succeed.
- The active default refund policy applies globally.
- Discount codes are percentage based.
- Seat availability is calculated from confirmed bookings and active non-expired holds.
- Notifications are represented by async observers and logs.
- H2 is in-memory, so data resets when the app restarts.

## 20. Reference Used

The pattern alignment was influenced by the referenced `awesome-low-level-design` movie ticket booking system, especially its explicit mention of Observer Pattern for updates and Strategy Pattern for pricing.

Reference:

```text
https://github.com/ashishps1/awesome-low-level-design/tree/main/solutions/java/src/movieticketbookingsystem
```

## 21. Suggested Demo Script

1. Start the app.
2. Open Postman and import the collection.
3. Run `Whole Flow APIs - Run In Order`.
4. Show that variables are captured automatically:
   - `cityId`
   - `theaterId`
   - `movieId`
   - `showId`
   - `seatId1`
   - `seatId2`
   - `holdId1`
   - `holdId2`
   - `bookingId`
5. Show H2 database tables.
6. Run the negative examples:
   - customer cannot create city
   - second customer cannot hold already held seats
7. Run tests with `./mvnw test`.
