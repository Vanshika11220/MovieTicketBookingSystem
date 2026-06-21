package com.dmg.MovieTicketBookingSystem.payment;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.dmg.MovieTicketBookingSystem.common.ApiException;
import com.dmg.MovieTicketBookingSystem.domain.Payment;
import com.dmg.MovieTicketBookingSystem.domain.enums.PaymentMethod;

@Component
public class PaymentProcessor {
	private final List<PaymentStrategy> paymentStrategies;

	public PaymentProcessor(List<PaymentStrategy> paymentStrategies) {
		this.paymentStrategies = paymentStrategies;
	}

	public Payment process(PaymentMethod paymentMethod, BigDecimal amount) {
		return paymentStrategies.stream()
				.filter(strategy -> strategy.supports(paymentMethod))
				.findFirst()
				.map(strategy -> strategy.pay(amount))
				.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST,
						"No payment strategy configured for " + paymentMethod));
	}
}
