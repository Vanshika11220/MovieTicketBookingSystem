package com.dmg.MovieTicketBookingSystem.pricing;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.dmg.MovieTicketBookingSystem.domain.DiscountCode;
import com.dmg.MovieTicketBookingSystem.domain.MovieShow;
import com.dmg.MovieTicketBookingSystem.domain.Seat;

public interface PricingPolicy {
	BigDecimal calculate(MovieShow movieShow, List<Seat> seats, Optional<DiscountCode> discountCode);
}
