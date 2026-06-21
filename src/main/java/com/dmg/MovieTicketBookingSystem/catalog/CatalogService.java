package com.dmg.MovieTicketBookingSystem.catalog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dmg.MovieTicketBookingSystem.catalog.CatalogDtos.SeatResponse;
import com.dmg.MovieTicketBookingSystem.catalog.CatalogDtos.ShowResponse;
import com.dmg.MovieTicketBookingSystem.catalog.CatalogDtos.ShowSeatsResponse;
import com.dmg.MovieTicketBookingSystem.common.NotFoundException;
import com.dmg.MovieTicketBookingSystem.domain.enums.BookingStatus;
import com.dmg.MovieTicketBookingSystem.domain.enums.HoldStatus;
import com.dmg.MovieTicketBookingSystem.domain.MovieShow;
import com.dmg.MovieTicketBookingSystem.repository.BookingRepository;
import com.dmg.MovieTicketBookingSystem.repository.MovieShowRepository;
import com.dmg.MovieTicketBookingSystem.repository.SeatHoldRepository;
import com.dmg.MovieTicketBookingSystem.repository.SeatRepository;

@Service
public class CatalogService {
	private final MovieShowRepository movieShowRepository;
	private final SeatRepository seatRepository;
	private final SeatHoldRepository seatHoldRepository;
	private final BookingRepository bookingRepository;

	public CatalogService(MovieShowRepository movieShowRepository, SeatRepository seatRepository,
			SeatHoldRepository seatHoldRepository, BookingRepository bookingRepository) {
		this.movieShowRepository = movieShowRepository;
		this.seatRepository = seatRepository;
		this.seatHoldRepository = seatHoldRepository;
		this.bookingRepository = bookingRepository;
	}

	@Transactional(readOnly = true)
	public List<ShowResponse> browseShows(Long cityId) {
		return movieShowRepository.browse(cityId, LocalDateTime.now()).stream()
				.map(show -> new ShowResponse(show.getId(), show.getMovie().getTitle(), show.getTheater().getCity().getName(),
						show.getTheater().getName(), show.getStartsAt(), show.getBasePrice()))
				.toList();
	}

	@Transactional(readOnly = true)
	public ShowSeatsResponse seatsForShow(Long showId) {
		MovieShow show = movieShowRepository.findById(showId).orElseThrow(() -> new NotFoundException("Show", showId));
		var seats = seatRepository.findByTheaterIdOrderByRowLabelAscSeatNumberAsc(show.getTheater().getId());
		List<Long> allSeatIds = seats.stream().map(seat -> seat.getId()).toList();
		Set<Long> held = seatHoldRepository.findActiveHolds(showId, allSeatIds, HoldStatus.ACTIVE, LocalDateTime.now())
				.stream().map(hold -> hold.getSeat().getId()).collect(Collectors.toSet());
		Set<Long> booked = Set.copyOf(
				bookingRepository.findConfirmedSeatIds(showId, allSeatIds, BookingStatus.CONFIRMED));
		return new ShowSeatsResponse(showId, seats.stream()
				.map(seat -> new SeatResponse(seat.getId(), seat.label(), seat.getSeatType(),
						!held.contains(seat.getId()) && !booked.contains(seat.getId())))
				.toList());
	}
}
