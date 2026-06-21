package com.dmg.MovieTicketBookingSystem.payment;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dmg.MovieTicketBookingSystem.domain.Payment;
import com.dmg.MovieTicketBookingSystem.domain.enums.PaymentMethod;

@Component
public class UpiPaymentStrategy implements PaymentStrategy {
	@Override
	public boolean supports(PaymentMethod paymentMethod) {
		return paymentMethod == PaymentMethod.UPI;
	}

	@Override
	public Payment pay(BigDecimal amount) {
		return new Payment("UPI-" + UUID.randomUUID(), amount);
	}
}
