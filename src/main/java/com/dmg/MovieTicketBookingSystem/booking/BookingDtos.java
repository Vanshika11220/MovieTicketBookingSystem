package com.dmg.MovieTicketBookingSystem.booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.dmg.MovieTicketBookingSystem.domain.BookingStatus;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public final class BookingDtos {
	private BookingDtos() {
	}

	public record HoldSeatsRequest(@NotNull Long showId, @NotEmpty List<Long> seatIds) {
	}

	public record HoldSeatsResponse(List<Long> holdIds, LocalDateTime expiresAt) {
	}

	public record ConfirmBookingRequest(@NotEmpty List<Long> holdIds, String discountCode) {
	}

	public record BookingResponse(Long bookingId, String bookingReference, BookingStatus status, BigDecimal amountPaid,
			BigDecimal refundAmount, List<String> seats) {
	}

	public record CancelBookingResponse(Long bookingId, BookingStatus status, BigDecimal refundAmount) {
	}
}
