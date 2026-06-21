package com.dmg.MovieTicketBookingSystem.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;

import com.dmg.MovieTicketBookingSystem.common.ApiException;
import com.dmg.MovieTicketBookingSystem.domain.DiscountCode;
import com.dmg.MovieTicketBookingSystem.domain.MovieShow;
import com.dmg.MovieTicketBookingSystem.domain.Seat;

@Component
public class DefaultPricingPolicy implements PricingPolicy {
	private final List<SeatPricingStrategy> seatPricingStrategies;
	private final List<PricingModifier> pricingModifiers;

	public DefaultPricingPolicy(List<SeatPricingStrategy> seatPricingStrategies, List<PricingModifier> pricingModifiers) {
		this.seatPricingStrategies = seatPricingStrategies;
		this.pricingModifiers = pricingModifiers;
	}

	@Override
	public BigDecimal calculate(MovieShow movieShow, List<Seat> seats, Optional<DiscountCode> discountCode) {
		BigDecimal subtotal = seats.stream()
				.map(seat -> priceSeat(movieShow, seat))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal total = subtotal;
		for (PricingModifier modifier : pricingModifiers) {
			total = modifier.apply(total, movieShow, discountCode);
		}
		return total.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
	}

	private BigDecimal priceSeat(MovieShow movieShow, Seat seat) {
		return seatPricingStrategies.stream()
				.filter(strategy -> strategy.supports(seat.getSeatType()))
				.findFirst()
				.map(strategy -> strategy.price(movieShow, seat))
				.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST,
						"No pricing strategy configured for seat type " + seat.getSeatType()));
	}
}
