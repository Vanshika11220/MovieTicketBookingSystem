package com.dmg.MovieTicketBookingSystem.pricing;

import java.math.BigDecimal;
import java.util.Optional;

import com.dmg.MovieTicketBookingSystem.domain.DiscountCode;
import com.dmg.MovieTicketBookingSystem.domain.MovieShow;

public interface PricingModifier {
	BigDecimal apply(BigDecimal currentTotal, MovieShow movieShow, Optional<DiscountCode> discountCode);
}
