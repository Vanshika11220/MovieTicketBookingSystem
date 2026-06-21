package com.dmg.MovieTicketBookingSystem.booking;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.BookingResponse;
import com.dmg.MovieTicketBookingSystem.domain.Booking;

@Component
public class BookingMapper {
	public BookingResponse toResponse(Booking booking) {
		List<String> seats = booking.getSeats().stream()
				.map(bookingSeat -> bookingSeat.getSeat().label())
				.sorted()
				.toList();
		return new BookingResponse(booking.getId(), booking.getBookingReference(), booking.getStatus(),
				booking.getAmountPaid(), booking.getRefundAmount(), seats);
	}
}
