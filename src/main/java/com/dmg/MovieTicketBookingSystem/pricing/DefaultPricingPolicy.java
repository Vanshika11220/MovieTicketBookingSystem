package com.dmg.MovieTicketBookingSystem.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.dmg.MovieTicketBookingSystem.domain.DiscountCode;
import com.dmg.MovieTicketBookingSystem.domain.MovieShow;
import com.dmg.MovieTicketBookingSystem.domain.Seat;
import com.dmg.MovieTicketBookingSystem.domain.SeatType;

@Component
public class DefaultPricingPolicy implements PricingPolicy {
	private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

	@Override
	public BigDecimal calculate(MovieShow movieShow, List<Seat> seats, Optional<DiscountCode> discountCode) {
		BigDecimal subtotal = seats.stream()
				.map(seat -> priceSeat(movieShow, seat))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal discount = discountCode
				.filter(code -> code.isActive() && !code.getValidUntil().isBefore(LocalDate.now()))
				.map(code -> subtotal.multiply(code.getPercentage()).divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP))
				.orElse(BigDecimal.ZERO);
		return subtotal.subtract(discount).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
	}

	private BigDecimal priceSeat(MovieShow movieShow, Seat seat) {
		BigDecimal price = movieShow.getBasePrice();
		if (seat.getSeatType() == SeatType.PREMIUM) {
			price = price.add(movieShow.getPremiumSurcharge());
		}
		DayOfWeek day = movieShow.getStartsAt().getDayOfWeek();
		if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
			price = price.multiply(movieShow.getWeekendMultiplier());
		}
		return price;
	}
}
