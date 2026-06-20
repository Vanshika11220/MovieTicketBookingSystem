package com.dmg.MovieTicketBookingSystem.refund;

import java.math.BigDecimal;

import com.dmg.MovieTicketBookingSystem.domain.Booking;

public interface RefundCalculator {
	BigDecimal calculateRefund(Booking booking);
}
