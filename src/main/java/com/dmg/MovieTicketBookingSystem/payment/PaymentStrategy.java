package com.dmg.MovieTicketBookingSystem.payment;

import java.math.BigDecimal;

import com.dmg.MovieTicketBookingSystem.domain.Payment;
import com.dmg.MovieTicketBookingSystem.domain.enums.PaymentMethod;

public interface PaymentStrategy {
	boolean supports(PaymentMethod paymentMethod);

	Payment pay(BigDecimal amount);
}
