package com.dmg.MovieTicketBookingSystem.booking;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.BookingResponse;
import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.CancelBookingResponse;
import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.ConfirmBookingRequest;
import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.HoldSeatsRequest;
import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.HoldSeatsResponse;
import com.dmg.MovieTicketBookingSystem.domain.enums.Role;
import com.dmg.MovieTicketBookingSystem.security.RoleRequired;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customer")
@RoleRequired(Role.CUSTOMER)
public class BookingController {
	private final BookingService bookingService;

	public BookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	@PostMapping("/holds")
	public HoldSeatsResponse holdSeats(@Valid @RequestBody HoldSeatsRequest request) {
		return bookingService.holdSeats(request);
	}

	@PostMapping("/bookings")
	public BookingResponse confirmBooking(@Valid @RequestBody ConfirmBookingRequest request) {
		return bookingService.confirmBooking(request);
	}

	@PostMapping("/bookings/{bookingId}/cancel")
	public CancelBookingResponse cancelBooking(@PathVariable Long bookingId) {
		return bookingService.cancelBooking(bookingId);
	}

	@GetMapping("/bookings")
	public List<BookingResponse> bookingHistory() {
		return bookingService.bookingHistory();
	}
}
