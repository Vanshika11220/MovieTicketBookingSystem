package com.dmg.MovieTicketBookingSystem.payment;

import java.math.BigDecimal;

import com.dmg.MovieTicketBookingSystem.domain.Payment;

public interface PaymentGateway {
	Payment charge(BigDecimal amount);
}
