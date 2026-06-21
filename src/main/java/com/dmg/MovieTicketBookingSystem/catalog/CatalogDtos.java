package com.dmg.MovieTicketBookingSystem.catalog;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.dmg.MovieTicketBookingSystem.domain.enums.SeatType;

public final class CatalogDtos {
	private CatalogDtos() {
	}

	public record ShowResponse(Long id, String movieTitle, String city, String theater, LocalDateTime startsAt,
			BigDecimal basePrice) {
	}

	public record SeatResponse(Long id, String label, SeatType seatType, boolean available) {
	}

	public record ShowSeatsResponse(Long showId, List<SeatResponse> seats) {
	}
}
