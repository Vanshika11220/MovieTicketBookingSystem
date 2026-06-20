package com.dmg.MovieTicketBookingSystem.admin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.dmg.MovieTicketBookingSystem.domain.SeatType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public final class AdminDtos {
	private AdminDtos() {
	}

	public record CreateCityRequest(@NotBlank String name) {
	}

	public record CreateTheaterRequest(@NotBlank String name, @NotBlank String address, @NotNull Long cityId) {
	}

	public record SeatDefinition(@NotBlank String rowLabel, @Min(1) int seatNumber, @NotNull SeatType seatType) {
	}

	public record CreateSeatLayoutRequest(@NotNull Long theaterId, @NotEmpty List<@Valid SeatDefinition> seats) {
	}

	public record CreateMovieRequest(@NotBlank String title, @Min(1) int durationMinutes, @NotBlank String language) {
	}

	public record CreateShowRequest(
			@NotNull Long movieId,
			@NotNull Long theaterId,
			@NotNull @Future LocalDateTime startsAt,
			@NotNull @DecimalMin("0.01") BigDecimal basePrice,
			@NotNull @DecimalMin("0.00") BigDecimal premiumSurcharge,
			@NotNull @DecimalMin("1.00") BigDecimal weekendMultiplier,
			@Min(1) int holdMinutes) {
	}

	public record CreateDiscountRequest(@NotBlank String code, @NotNull @DecimalMin("0.00") BigDecimal percentage,
			@NotNull LocalDate validUntil, boolean active) {
	}

	public record CreateRefundPolicyRequest(@NotBlank String name, @Min(0) long cutoffMinutesBeforeShow,
			@NotNull @DecimalMin("0.00") BigDecimal refundPercentage, boolean activeDefault) {
	}

	public record IdResponse(Long id) {
	}
}
