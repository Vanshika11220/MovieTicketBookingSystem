package com.dmg.MovieTicketBookingSystem.booking;

import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.dmg.MovieTicketBookingSystem.common.ApiException;
import com.dmg.MovieTicketBookingSystem.domain.MovieShow;
import com.dmg.MovieTicketBookingSystem.domain.Seat;
import com.dmg.MovieTicketBookingSystem.domain.enums.BookingStatus;
import com.dmg.MovieTicketBookingSystem.domain.enums.HoldStatus;
import com.dmg.MovieTicketBookingSystem.repository.BookingRepository;
import com.dmg.MovieTicketBookingSystem.repository.SeatHoldRepository;
import com.dmg.MovieTicketBookingSystem.repository.SeatRepository;

@Service
public class SeatAvailabilityService {
	private final SeatRepository seatRepository;
	private final SeatHoldRepository seatHoldRepository;
	private final BookingRepository bookingRepository;

	public SeatAvailabilityService(SeatRepository seatRepository, SeatHoldRepository seatHoldRepository,
			BookingRepository bookingRepository) {
		this.seatRepository = seatRepository;
		this.seatHoldRepository = seatHoldRepository;
		this.bookingRepository = bookingRepository;
	}

	public List<Seat> lockSeats(List<Long> seatIds) {
		List<Seat> seats = seatRepository.lockAllByIdIn(seatIds.stream().distinct().sorted().toList());
		if (seats.size() != seatIds.stream().distinct().count()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "One or more seats do not exist");
		}
		return seats.stream().sorted(Comparator.comparing(Seat::getId)).toList();
	}

	public void validateSeatsBelongToShowTheater(MovieShow show, List<Seat> seats) {
		boolean allMatch = seats.stream().allMatch(seat -> seat.getTheater().getId().equals(show.getTheater().getId()));
		if (!allMatch) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "All seats must belong to the show's theater");
		}
	}

	public void assertSeatsNotBooked(Long showId, List<Long> seatIds) {
		if (bookingRepository.anyConfirmedSeat(showId, seatIds, BookingStatus.CONFIRMED)) {
			throw new ApiException(HttpStatus.CONFLICT, "One or more seats are already booked");
		}
	}

	public void assertSeatsNotHeld(Long showId, List<Long> seatIds) {
		if (!seatHoldRepository.findActiveHolds(showId, seatIds, HoldStatus.ACTIVE, java.time.LocalDateTime.now())
				.isEmpty()) {
			throw new ApiException(HttpStatus.CONFLICT, "One or more seats are temporarily held");
		}
	}
}
