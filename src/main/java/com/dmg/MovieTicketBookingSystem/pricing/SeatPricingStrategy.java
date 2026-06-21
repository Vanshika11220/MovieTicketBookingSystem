package com.dmg.MovieTicketBookingSystem.pricing;

import java.math.BigDecimal;

import com.dmg.MovieTicketBookingSystem.domain.MovieShow;
import com.dmg.MovieTicketBookingSystem.domain.Seat;
import com.dmg.MovieTicketBookingSystem.domain.enums.SeatType;

public interface SeatPricingStrategy {
	boolean supports(SeatType seatType);

	BigDecimal price(MovieShow movieShow, Seat seat);
}
