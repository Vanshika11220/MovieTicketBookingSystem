package com.dmg.MovieTicketBookingSystem.booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.BookingResponse;
import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.ConfirmBookingRequest;
import com.dmg.MovieTicketBookingSystem.common.ApiException;
import com.dmg.MovieTicketBookingSystem.domain.Booking;
import com.dmg.MovieTicketBookingSystem.domain.DiscountCode;
import com.dmg.MovieTicketBookingSystem.domain.MovieShow;
import com.dmg.MovieTicketBookingSystem.domain.Payment;
import com.dmg.MovieTicketBookingSystem.domain.Seat;
import com.dmg.MovieTicketBookingSystem.domain.SeatHold;
import com.dmg.MovieTicketBookingSystem.domain.UserAccount;
import com.dmg.MovieTicketBookingSystem.domain.enums.HoldStatus;
import com.dmg.MovieTicketBookingSystem.domain.enums.PaymentMethod;
import com.dmg.MovieTicketBookingSystem.notification.BookingNotificationSubject;
import com.dmg.MovieTicketBookingSystem.payment.PaymentProcessor;
import com.dmg.MovieTicketBookingSystem.pricing.PricingPolicy;
import com.dmg.MovieTicketBookingSystem.repository.BookingRepository;
import com.dmg.MovieTicketBookingSystem.repository.DiscountCodeRepository;
import com.dmg.MovieTicketBookingSystem.repository.SeatHoldRepository;

@Service
public class BookingConfirmationService {
	private final SeatHoldRepository seatHoldRepository;
	private final BookingRepository bookingRepository;
	private final DiscountCodeRepository discountCodeRepository;
	private final SeatAvailabilityService seatAvailabilityService;
	private final SeatHoldService seatHoldService;
	private final CustomerResolver customerResolver;
	private final PricingPolicy pricingPolicy;
	private final PaymentProcessor paymentProcessor;
	private final BookingNotificationSubject notificationSubject;
	private final BookingMapper bookingMapper;

	public BookingConfirmationService(SeatHoldRepository seatHoldRepository, BookingRepository bookingRepository,
			DiscountCodeRepository discountCodeRepository, SeatAvailabilityService seatAvailabilityService,
			SeatHoldService seatHoldService, CustomerResolver customerResolver, PricingPolicy pricingPolicy,
			PaymentProcessor paymentProcessor, BookingNotificationSubject notificationSubject,
			BookingMapper bookingMapper) {
		this.seatHoldRepository = seatHoldRepository;
		this.bookingRepository = bookingRepository;
		this.discountCodeRepository = discountCodeRepository;
		this.seatAvailabilityService = seatAvailabilityService;
		this.seatHoldService = seatHoldService;
		this.customerResolver = customerResolver;
		this.pricingPolicy = pricingPolicy;
		this.paymentProcessor = paymentProcessor;
		this.notificationSubject = notificationSubject;
		this.bookingMapper = bookingMapper;
	}

	@Transactional
	public BookingResponse confirmBooking(ConfirmBookingRequest request) {
		UserAccount customer = customerResolver.currentCustomer();
		seatHoldService.expireOldHolds();
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
		List<Seat> seats = seatAvailabilityService.lockSeats(holds.stream().map(hold -> hold.getSeat().getId()).toList());
		seatAvailabilityService.assertSeatsNotBooked(show.getId(), seats.stream().map(Seat::getId).toList());

		Optional<DiscountCode> discount = Optional.ofNullable(request.discountCode())
				.filter(code -> !code.isBlank())
				.flatMap(discountCodeRepository::findByCodeIgnoreCase);
		var amount = pricingPolicy.calculate(show, seats, discount);
		PaymentMethod paymentMethod = Optional.ofNullable(request.paymentMethod()).orElse(PaymentMethod.CARD);
		Payment payment = paymentProcessor.process(paymentMethod, amount);
		Booking booking = new Booking(show, customer, payment, amount, request.discountCode());
		seats.forEach(booking::addSeat);
		holds.forEach(SeatHold::markConfirmed);
		Booking saved = bookingRepository.save(booking);
		notificationSubject.bookingConfirmed(saved.getId(), saved.getBookingReference());
		return bookingMapper.toResponse(saved);
	}
}
