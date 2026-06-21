package com.dmg.MovieTicketBookingSystem.payment;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dmg.MovieTicketBookingSystem.domain.Payment;
import com.dmg.MovieTicketBookingSystem.domain.enums.PaymentMethod;

@Component
public class CardPaymentStrategy implements PaymentStrategy {
	@Override
	public boolean supports(PaymentMethod paymentMethod) {
		return paymentMethod == PaymentMethod.CARD;
	}

	@Override
	public Payment pay(BigDecimal amount) {
		return new Payment("CARD-" + UUID.randomUUID(), amount);
	}
}
