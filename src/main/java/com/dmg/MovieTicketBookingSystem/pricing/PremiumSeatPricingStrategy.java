package com.dmg.MovieTicketBookingSystem.pricing;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.dmg.MovieTicketBookingSystem.domain.MovieShow;
import com.dmg.MovieTicketBookingSystem.domain.Seat;
import com.dmg.MovieTicketBookingSystem.domain.enums.SeatType;

@Component
public class PremiumSeatPricingStrategy implements SeatPricingStrategy {
	@Override
	public boolean supports(SeatType seatType) {
		return seatType == SeatType.PREMIUM;
	}

	@Override
	public BigDecimal price(MovieShow movieShow, Seat seat) {
		return movieShow.getBasePrice().add(movieShow.getPremiumSurcharge());
	}
}
