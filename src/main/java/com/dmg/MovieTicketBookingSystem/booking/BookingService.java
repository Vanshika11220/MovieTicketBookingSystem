package com.dmg.MovieTicketBookingSystem.booking;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.BookingResponse;
import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.CancelBookingResponse;
import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.ConfirmBookingRequest;
import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.HoldSeatsRequest;
import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.HoldSeatsResponse;
import com.dmg.MovieTicketBookingSystem.common.ApiException;
import com.dmg.MovieTicketBookingSystem.common.NotFoundException;
import com.dmg.MovieTicketBookingSystem.domain.Booking;
import com.dmg.MovieTicketBookingSystem.domain.BookingStatus;
import com.dmg.MovieTicketBookingSystem.domain.DiscountCode;
import com.dmg.MovieTicketBookingSystem.domain.HoldStatus;
import com.dmg.MovieTicketBookingSystem.domain.MovieShow;
import com.dmg.MovieTicketBookingSystem.domain.Payment;
import com.dmg.MovieTicketBookingSystem.domain.Seat;
import com.dmg.MovieTicketBookingSystem.domain.SeatHold;
import com.dmg.MovieTicketBookingSystem.domain.UserAccount;
import com.dmg.MovieTicketBookingSystem.notification.BookingCancelledEvent;
import com.dmg.MovieTicketBookingSystem.notification.BookingConfirmedEvent;
import com.dmg.MovieTicketBookingSystem.payment.PaymentGateway;
import com.dmg.MovieTicketBookingSystem.pricing.PricingPolicy;
import com.dmg.MovieTicketBookingSystem.refund.RefundCalculator;
import com.dmg.MovieTicketBookingSystem.repository.BookingRepository;
import com.dmg.MovieTicketBookingSystem.repository.DiscountCodeRepository;
import com.dmg.MovieTicketBookingSystem.repository.MovieShowRepository;
import com.dmg.MovieTicketBookingSystem.repository.SeatHoldRepository;
import com.dmg.MovieTicketBookingSystem.repository.SeatRepository;
import com.dmg.MovieTicketBookingSystem.repository.UserAccountRepository;
import com.dmg.MovieTicketBookingSystem.security.UserContext;

@Service
public class BookingService {
	private final MovieShowRepository movieShowRepository;
	private final SeatRepository seatRepository;
	private final SeatHoldRepository seatHoldRepository;
	private final BookingRepository bookingRepository;
	private final UserAccountRepository userAccountRepository;
	private final DiscountCodeRepository discountCodeRepository;
	private final PricingPolicy pricingPolicy;
	private final PaymentGateway paymentGateway;
	private final RefundCalculator refundCalculator;
	private final ApplicationEventPublisher eventPublisher;
	private final UserContext userContext;

	public BookingService(MovieShowRepository movieShowRepository, SeatRepository seatRepository,
			SeatHoldRepository seatHoldRepository, BookingRepository bookingRepository,
			UserAccountRepository userAccountRepository, DiscountCodeRepository discountCodeRepository,
			PricingPolicy pricingPolicy, PaymentGateway paymentGateway, RefundCalculator refundCalculator,
			ApplicationEventPublisher eventPublisher, UserContext userContext) {
		this.movieShowRepository = movieShowRepository;
		this.seatRepository = seatRepository;
		this.seatHoldRepository = seatHoldRepository;
		this.bookingRepository = bookingRepository;
		this.userAccountRepository = userAccountRepository;
		this.discountCodeRepository = discountCodeRepository;
		this.pricingPolicy = pricingPolicy;
		this.paymentGateway = paymentGateway;
		this.refundCalculator = refundCalculator;
		this.eventPublisher = eventPublisher;
		this.userContext = userContext;
	}

	@Transactional
	public HoldSeatsResponse holdSeats(HoldSeatsRequest request) {
		UserAccount customer = currentCustomer();
		MovieShow show = movieShowRepository.findById(request.showId())
				.orElseThrow(() -> new NotFoundException("Show", request.showId()));
		List<Seat> seats = lockedSeats(request.seatIds());
		validateSeatsBelongToShowTheater(show, seats);
		expireOldHolds();

		List<Long> seatIds = seats.stream().map(Seat::getId).toList();
		if (bookingRepository.anyConfirmedSeat(show.getId(), seatIds, BookingStatus.CONFIRMED)) {
			throw new ApiException(HttpStatus.CONFLICT, "One or more seats are already booked");
		}
		if (!seatHoldRepository.findActiveHolds(show.getId(), seatIds, HoldStatus.ACTIVE, LocalDateTime.now()).isEmpty()) {
			throw new ApiException(HttpStatus.CONFLICT, "One or more seats are temporarily held");
		}

		LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(show.getHoldMinutes());
		List<SeatHold> holds = seatHoldRepository.saveAll(seats.stream()
				.map(seat -> new SeatHold(show, seat, customer, expiresAt))
				.toList());
		return new HoldSeatsResponse(holds.stream().map(SeatHold::getId).toList(), expiresAt);
	}

	@Transactional
	public BookingResponse confirmBooking(ConfirmBookingRequest request) {
		UserAccount customer = currentCustomer();
		expireOldHolds();
		List<SeatHold> holds = seatHoldRepository.findActiveByIds(request.holdIds(), HoldStatus.ACTIVE);
		if (holds.size() != request.holdIds().size()) {
			throw new ApiException(HttpStatus.CONFLICT, "All holds must be active to confirm");
		}
		holds.forEach(hold -> {
			if (!hold.getCustomer().getId().equals(customer.getId())) {
				throw new ApiException(HttpStatus.FORBIDDEN, "Cannot confirm another customer's hold");
			}
			if (!hold.getExpiresAt().isAfter(LocalDateTime.now())) {
				throw new ApiException(HttpStatus.CONFLICT, "Hold has expired");
			}
		});

		MovieShow show = holds.get(0).getMovieShow();
		boolean sameShow = holds.stream().allMatch(hold -> hold.getMovieShow().getId().equals(show.getId()));
		if (!sameShow) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "All holds must belong to the same show");
		}
		List<Seat> seats = lockedSeats(holds.stream().map(hold -> hold.getSeat().getId()).toList());
		if (bookingRepository.anyConfirmedSeat(show.getId(), seats.stream().map(Seat::getId).toList(), BookingStatus.CONFIRMED)) {
			throw new ApiException(HttpStatus.CONFLICT, "One or more seats are already booked");
		}

		Optional<DiscountCode> discount = Optional.ofNullable(request.discountCode())
				.filter(code -> !code.isBlank())
				.flatMap(discountCodeRepository::findByCodeIgnoreCase);
		var amount = pricingPolicy.calculate(show, seats, discount);
		Payment payment = paymentGateway.charge(amount);
		Booking booking = new Booking(show, customer, payment, amount, request.discountCode());
		seats.forEach(booking::addSeat);
		holds.forEach(SeatHold::markConfirmed);
		Booking saved = bookingRepository.save(booking);
		eventPublisher.publishEvent(new BookingConfirmedEvent(saved.getId(), saved.getBookingReference()));
		return toResponse(saved);
	}

	@Transactional
	public CancelBookingResponse cancelBooking(Long bookingId) {
		UserAccount customer = currentCustomer();
		Booking booking = bookingRepository.findDetailedById(bookingId).orElseThrow(() -> new NotFoundException("Booking", bookingId));
		if (!booking.getCustomer().getId().equals(customer.getId())) {
			throw new ApiException(HttpStatus.FORBIDDEN, "Cannot cancel another customer's booking");
		}
		if (booking.getStatus() == BookingStatus.CANCELLED) {
			throw new ApiException(HttpStatus.CONFLICT, "Booking is already cancelled");
		}
		var refund = refundCalculator.calculateRefund(booking);
		booking.cancel(refund);
		eventPublisher.publishEvent(new BookingCancelledEvent(booking.getId(), booking.getBookingReference()));
		return new CancelBookingResponse(booking.getId(), booking.getStatus(), booking.getRefundAmount());
	}

	@Transactional(readOnly = true)
	public List<BookingResponse> bookingHistory() {
		UserAccount customer = currentCustomer();
		return bookingRepository.findByCustomerIdOrderByConfirmedAtDesc(customer.getId()).stream()
				.map(this::toResponse)
				.toList();
	}

	@Scheduled(fixedDelay = 60_000)
	@Transactional
	public void expireOldHolds() {
		seatHoldRepository.expireOldHolds(LocalDateTime.now());
	}

	private UserAccount currentCustomer() {
		Long userId = userContext.currentUser().id();
		return userAccountRepository.findById(userId).orElseThrow(() -> new NotFoundException("User", userId));
	}

	private List<Seat> lockedSeats(List<Long> seatIds) {
		List<Seat> seats = seatRepository.lockAllByIdIn(seatIds.stream().distinct().sorted().toList());
		if (seats.size() != seatIds.stream().distinct().count()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "One or more seats do not exist");
		}
		return seats.stream().sorted(Comparator.comparing(Seat::getId)).toList();
	}

	private void validateSeatsBelongToShowTheater(MovieShow show, List<Seat> seats) {
		boolean allMatch = seats.stream().allMatch(seat -> seat.getTheater().getId().equals(show.getTheater().getId()));
		if (!allMatch) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "All seats must belong to the show's theater");
		}
	}

	private BookingResponse toResponse(Booking booking) {
		List<String> seats = booking.getSeats().stream()
				.map(bookingSeat -> bookingSeat.getSeat().label())
				.sorted()
				.toList();
		return new BookingResponse(booking.getId(), booking.getBookingReference(), booking.getStatus(),
				booking.getAmountPaid(), booking.getRefundAmount(), seats);
	}
}
