package com.dmg.MovieTicketBookingSystem.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.dmg.MovieTicketBookingSystem.domain.DiscountCode;
import com.dmg.MovieTicketBookingSystem.domain.MovieShow;

@Order(20)
@Component
public class DiscountPricingModifier implements PricingModifier {
	private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

	@Override
	public BigDecimal apply(BigDecimal currentTotal, MovieShow movieShow, Optional<DiscountCode> discountCode) {
		BigDecimal discount = discountCode
				.filter(code -> code.isActive() && !code.getValidUntil().isBefore(LocalDate.now()))
				.map(code -> currentTotal.multiply(code.getPercentage()).divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP))
				.orElse(BigDecimal.ZERO);
		return currentTotal.subtract(discount);
	}
}
