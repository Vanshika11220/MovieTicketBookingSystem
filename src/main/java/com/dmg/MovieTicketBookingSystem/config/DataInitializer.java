package com.dmg.MovieTicketBookingSystem.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dmg.MovieTicketBookingSystem.domain.City;
import com.dmg.MovieTicketBookingSystem.domain.DiscountCode;
import com.dmg.MovieTicketBookingSystem.domain.Movie;
import com.dmg.MovieTicketBookingSystem.domain.MovieShow;
import com.dmg.MovieTicketBookingSystem.domain.RefundPolicy;
import com.dmg.MovieTicketBookingSystem.domain.Role;
import com.dmg.MovieTicketBookingSystem.domain.Seat;
import com.dmg.MovieTicketBookingSystem.domain.SeatType;
import com.dmg.MovieTicketBookingSystem.domain.Theater;
import com.dmg.MovieTicketBookingSystem.domain.UserAccount;
import com.dmg.MovieTicketBookingSystem.repository.CityRepository;
import com.dmg.MovieTicketBookingSystem.repository.DiscountCodeRepository;
import com.dmg.MovieTicketBookingSystem.repository.MovieRepository;
import com.dmg.MovieTicketBookingSystem.repository.MovieShowRepository;
import com.dmg.MovieTicketBookingSystem.repository.RefundPolicyRepository;
import com.dmg.MovieTicketBookingSystem.repository.SeatRepository;
import com.dmg.MovieTicketBookingSystem.repository.TheaterRepository;
import com.dmg.MovieTicketBookingSystem.repository.UserAccountRepository;

@Component
public class DataInitializer implements CommandLineRunner {
	private final UserAccountRepository userAccountRepository;
	private final CityRepository cityRepository;
	private final TheaterRepository theaterRepository;
	private final SeatRepository seatRepository;
	private final MovieRepository movieRepository;
	private final MovieShowRepository movieShowRepository;
	private final DiscountCodeRepository discountCodeRepository;
	private final RefundPolicyRepository refundPolicyRepository;

	public DataInitializer(UserAccountRepository userAccountRepository, CityRepository cityRepository,
			TheaterRepository theaterRepository, SeatRepository seatRepository, MovieRepository movieRepository,
			MovieShowRepository movieShowRepository, DiscountCodeRepository discountCodeRepository,
			RefundPolicyRepository refundPolicyRepository) {
		this.userAccountRepository = userAccountRepository;
		this.cityRepository = cityRepository;
		this.theaterRepository = theaterRepository;
		this.seatRepository = seatRepository;
		this.movieRepository = movieRepository;
		this.movieShowRepository = movieShowRepository;
		this.discountCodeRepository = discountCodeRepository;
		this.refundPolicyRepository = refundPolicyRepository;
	}

	@Override
	@Transactional
	public void run(String... args) {
		if (userAccountRepository.count() > 0) {
			return;
		}
		userAccountRepository.save(new UserAccount("admin@example.com", Role.ADMIN));
		userAccountRepository.save(new UserAccount("customer@example.com", Role.CUSTOMER));
		userAccountRepository.save(new UserAccount("customer2@example.com", Role.CUSTOMER));

		City bengaluru = cityRepository.save(new City("Bengaluru"));
		Theater theater = theaterRepository.save(new Theater("Orion Screen 1", "Malleshwaram", bengaluru));

		List<Seat> seats = new ArrayList<>();
		for (String row : List.of("A", "B", "C")) {
			for (int number = 1; number <= 8; number++) {
				SeatType type = "C".equals(row) ? SeatType.PREMIUM : SeatType.REGULAR;
				seats.add(new Seat(theater, row, number, type));
			}
		}
		seatRepository.saveAll(seats);

		Movie movie = movieRepository.save(new Movie("The Spring Heist", 132, "English"));
		movieShowRepository.save(new MovieShow(movie, theater, LocalDateTime.now().plusDays(2),
				new BigDecimal("250.00"), new BigDecimal("100.00"), new BigDecimal("1.20"), 10));
		discountCodeRepository.save(new DiscountCode("WELCOME10", new BigDecimal("10.00"), LocalDate.now().plusMonths(1), true));
		refundPolicyRepository.save(new RefundPolicy("Full refund until 2 hours before show", 120, new BigDecimal("100.00"), true));
	}
}
