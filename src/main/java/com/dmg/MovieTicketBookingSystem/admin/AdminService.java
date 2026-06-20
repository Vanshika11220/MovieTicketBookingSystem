package com.dmg.MovieTicketBookingSystem.admin;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dmg.MovieTicketBookingSystem.admin.AdminDtos.CreateCityRequest;
import com.dmg.MovieTicketBookingSystem.admin.AdminDtos.CreateDiscountRequest;
import com.dmg.MovieTicketBookingSystem.admin.AdminDtos.CreateMovieRequest;
import com.dmg.MovieTicketBookingSystem.admin.AdminDtos.CreateRefundPolicyRequest;
import com.dmg.MovieTicketBookingSystem.admin.AdminDtos.CreateSeatLayoutRequest;
import com.dmg.MovieTicketBookingSystem.admin.AdminDtos.CreateShowRequest;
import com.dmg.MovieTicketBookingSystem.admin.AdminDtos.CreateTheaterRequest;
import com.dmg.MovieTicketBookingSystem.common.NotFoundException;
import com.dmg.MovieTicketBookingSystem.domain.City;
import com.dmg.MovieTicketBookingSystem.domain.DiscountCode;
import com.dmg.MovieTicketBookingSystem.domain.Movie;
import com.dmg.MovieTicketBookingSystem.domain.MovieShow;
import com.dmg.MovieTicketBookingSystem.domain.RefundPolicy;
import com.dmg.MovieTicketBookingSystem.domain.Seat;
import com.dmg.MovieTicketBookingSystem.domain.Theater;
import com.dmg.MovieTicketBookingSystem.repository.CityRepository;
import com.dmg.MovieTicketBookingSystem.repository.DiscountCodeRepository;
import com.dmg.MovieTicketBookingSystem.repository.MovieRepository;
import com.dmg.MovieTicketBookingSystem.repository.MovieShowRepository;
import com.dmg.MovieTicketBookingSystem.repository.RefundPolicyRepository;
import com.dmg.MovieTicketBookingSystem.repository.SeatRepository;
import com.dmg.MovieTicketBookingSystem.repository.TheaterRepository;

@Service
public class AdminService {
	private final CityRepository cityRepository;
	private final TheaterRepository theaterRepository;
	private final SeatRepository seatRepository;
	private final MovieRepository movieRepository;
	private final MovieShowRepository movieShowRepository;
	private final DiscountCodeRepository discountCodeRepository;
	private final RefundPolicyRepository refundPolicyRepository;

	public AdminService(CityRepository cityRepository, TheaterRepository theaterRepository, SeatRepository seatRepository,
			MovieRepository movieRepository, MovieShowRepository movieShowRepository,
			DiscountCodeRepository discountCodeRepository, RefundPolicyRepository refundPolicyRepository) {
		this.cityRepository = cityRepository;
		this.theaterRepository = theaterRepository;
		this.seatRepository = seatRepository;
		this.movieRepository = movieRepository;
		this.movieShowRepository = movieShowRepository;
		this.discountCodeRepository = discountCodeRepository;
		this.refundPolicyRepository = refundPolicyRepository;
	}

	@Transactional
	public Long createCity(CreateCityRequest request) {
		return cityRepository.save(new City(request.name())).getId();
	}

	@Transactional
	public Long createTheater(CreateTheaterRequest request) {
		City city = cityRepository.findById(request.cityId()).orElseThrow(() -> new NotFoundException("City", request.cityId()));
		return theaterRepository.save(new Theater(request.name(), request.address(), city)).getId();
	}

	@Transactional
	public List<Long> createSeatLayout(CreateSeatLayoutRequest request) {
		Theater theater = theaterRepository.findById(request.theaterId())
				.orElseThrow(() -> new NotFoundException("Theater", request.theaterId()));
		return seatRepository.saveAll(request.seats().stream()
				.map(seat -> new Seat(theater, seat.rowLabel(), seat.seatNumber(), seat.seatType()))
				.toList()).stream().map(Seat::getId).toList();
	}

	@Transactional
	public Long createMovie(CreateMovieRequest request) {
		return movieRepository.save(new Movie(request.title(), request.durationMinutes(), request.language())).getId();
	}

	@Transactional
	public Long createShow(CreateShowRequest request) {
		Movie movie = movieRepository.findById(request.movieId()).orElseThrow(() -> new NotFoundException("Movie", request.movieId()));
		Theater theater = theaterRepository.findById(request.theaterId())
				.orElseThrow(() -> new NotFoundException("Theater", request.theaterId()));
		return movieShowRepository.save(new MovieShow(movie, theater, request.startsAt(), request.basePrice(),
				request.premiumSurcharge(), request.weekendMultiplier(), request.holdMinutes())).getId();
	}

	@Transactional
	public Long createDiscount(CreateDiscountRequest request) {
		return discountCodeRepository.save(new DiscountCode(request.code(), request.percentage(), request.validUntil(),
				request.active())).getId();
	}

	@Transactional
	public Long createRefundPolicy(CreateRefundPolicyRequest request) {
		return refundPolicyRepository.save(new RefundPolicy(request.name(), request.cutoffMinutesBeforeShow(),
				request.refundPercentage(), request.activeDefault())).getId();
	}
}
