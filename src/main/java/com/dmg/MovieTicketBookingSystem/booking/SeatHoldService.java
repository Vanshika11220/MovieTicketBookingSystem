package com.dmg.MovieTicketBookingSystem.booking;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.HoldSeatsRequest;
import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.HoldSeatsResponse;
import com.dmg.MovieTicketBookingSystem.common.NotFoundException;
import com.dmg.MovieTicketBookingSystem.domain.MovieShow;
import com.dmg.MovieTicketBookingSystem.domain.Seat;
import com.dmg.MovieTicketBookingSystem.domain.SeatHold;
import com.dmg.MovieTicketBookingSystem.domain.UserAccount;
import com.dmg.MovieTicketBookingSystem.repository.MovieShowRepository;
import com.dmg.MovieTicketBookingSystem.repository.SeatHoldRepository;
import com.dmg.MovieTicketBookingSystem.security.UserContext;

@Service
public class SeatHoldService {
	private final MovieShowRepository movieShowRepository;
	private final SeatHoldRepository seatHoldRepository;
	private final SeatAvailabilityService seatAvailabilityService;
	private final CustomerResolver customerResolver;

	public SeatHoldService(MovieShowRepository movieShowRepository, SeatHoldRepository seatHoldRepository,
			SeatAvailabilityService seatAvailabilityService, CustomerResolver customerResolver) {
		this.movieShowRepository = movieShowRepository;
		this.seatHoldRepository = seatHoldRepository;
		this.seatAvailabilityService = seatAvailabilityService;
		this.customerResolver = customerResolver;
	}

	@Transactional
	public HoldSeatsResponse holdSeats(HoldSeatsRequest request) {
		UserAccount customer = customerResolver.currentCustomer();
		MovieShow show = movieShowRepository.findById(request.showId())
				.orElseThrow(() -> new NotFoundException("Show", request.showId()));
		List<Seat> seats = seatAvailabilityService.lockSeats(request.seatIds());
		seatAvailabilityService.validateSeatsBelongToShowTheater(show, seats);
		expireOldHolds();

		List<Long> seatIds = seats.stream().map(Seat::getId).toList();
		seatAvailabilityService.assertSeatsNotBooked(show.getId(), seatIds);
		seatAvailabilityService.assertSeatsNotHeld(show.getId(), seatIds);

		LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(show.getHoldMinutes());
		List<SeatHold> holds = seatHoldRepository.saveAll(seats.stream()
				.map(seat -> new SeatHold(show, seat, customer, expiresAt))
				.toList());
		return new HoldSeatsResponse(holds.stream().map(SeatHold::getId).toList(), expiresAt);
	}

	@Scheduled(fixedDelay = 60_000)
	@Transactional
	public void expireOldHolds() {
		seatHoldRepository.expireOldHolds(LocalDateTime.now());
	}
}
