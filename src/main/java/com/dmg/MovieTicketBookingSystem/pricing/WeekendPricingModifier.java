package com.dmg.MovieTicketBookingSystem.pricing;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.Optional;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.dmg.MovieTicketBookingSystem.domain.DiscountCode;
import com.dmg.MovieTicketBookingSystem.domain.MovieShow;

@Order(10)
@Component
public class WeekendPricingModifier implements PricingModifier {
	@Override
	public BigDecimal apply(BigDecimal currentTotal, MovieShow movieShow, Optional<DiscountCode> discountCode) {
		DayOfWeek day = movieShow.getStartsAt().getDayOfWeek();
		if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
			return currentTotal.multiply(movieShow.getWeekendMultiplier());
		}
		return currentTotal;
	}
}
