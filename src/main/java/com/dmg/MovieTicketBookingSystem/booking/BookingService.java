package com.dmg.MovieTicketBookingSystem.booking;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.BookingResponse;
import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.CancelBookingResponse;
import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.ConfirmBookingRequest;
import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.HoldSeatsRequest;
import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.HoldSeatsResponse;

@Service
public class BookingService {
	private final SeatHoldService seatHoldService;
	private final BookingConfirmationService bookingConfirmationService;
	private final BookingCancellationService bookingCancellationService;
	private final BookingQueryService bookingQueryService;

	public BookingService(SeatHoldService seatHoldService, BookingConfirmationService bookingConfirmationService,
			BookingCancellationService bookingCancellationService, BookingQueryService bookingQueryService) {
		this.seatHoldService = seatHoldService;
		this.bookingConfirmationService = bookingConfirmationService;
		this.bookingCancellationService = bookingCancellationService;
		this.bookingQueryService = bookingQueryService;
	}

	public HoldSeatsResponse holdSeats(HoldSeatsRequest request) {
		return seatHoldService.holdSeats(request);
	}

	public BookingResponse confirmBooking(ConfirmBookingRequest request) {
		return bookingConfirmationService.confirmBooking(request);
	}

	public CancelBookingResponse cancelBooking(Long bookingId) {
		return bookingCancellationService.cancelBooking(bookingId);
	}

	public List<BookingResponse> bookingHistory() {
		return bookingQueryService.bookingHistory();
	}
}
