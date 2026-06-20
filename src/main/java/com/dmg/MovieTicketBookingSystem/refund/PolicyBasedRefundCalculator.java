package com.dmg.MovieTicketBookingSystem.refund;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.dmg.MovieTicketBookingSystem.domain.Booking;
import com.dmg.MovieTicketBookingSystem.domain.RefundPolicy;
import com.dmg.MovieTicketBookingSystem.repository.RefundPolicyRepository;

@Component
public class PolicyBasedRefundCalculator implements RefundCalculator {
	private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
	private final RefundPolicyRepository refundPolicyRepository;

	public PolicyBasedRefundCalculator(RefundPolicyRepository refundPolicyRepository) {
		this.refundPolicyRepository = refundPolicyRepository;
	}

	@Override
	public BigDecimal calculateRefund(Booking booking) {
		RefundPolicy policy = refundPolicyRepository.findFirstByActiveDefaultTrue()
				.orElse(new RefundPolicy("No refund", Long.MAX_VALUE, BigDecimal.ZERO, true));
		long minutesBeforeShow = Duration.between(LocalDateTime.now(), booking.getMovieShow().getStartsAt()).toMinutes();
		if (minutesBeforeShow < policy.getCutoffMinutesBeforeShow()) {
			return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
		}
		return booking.getAmountPaid()
				.multiply(policy.getRefundPercentage())
				.divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
	}
}
