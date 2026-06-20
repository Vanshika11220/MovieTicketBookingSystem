package com.dmg.MovieTicketBookingSystem.payment;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dmg.MovieTicketBookingSystem.domain.Payment;

@Component
public class InMemoryPaymentGateway implements PaymentGateway {
	@Override
	public Payment charge(BigDecimal amount) {
		return new Payment("PAY-" + UUID.randomUUID(), amount);
	}
}
