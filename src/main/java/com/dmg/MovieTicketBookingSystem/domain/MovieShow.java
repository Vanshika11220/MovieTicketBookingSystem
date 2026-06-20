package com.dmg.MovieTicketBookingSystem.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class MovieShow {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Movie movie;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Theater theater;

	@Column(nullable = false)
	private LocalDateTime startsAt;

	@Column(nullable = false)
	private BigDecimal basePrice;

	@Column(nullable = false)
	private BigDecimal premiumSurcharge;

	@Column(nullable = false)
	private BigDecimal weekendMultiplier;

	private int holdMinutes;

	protected MovieShow() {
	}

	public MovieShow(Movie movie, Theater theater, LocalDateTime startsAt, BigDecimal basePrice,
			BigDecimal premiumSurcharge, BigDecimal weekendMultiplier, int holdMinutes) {
		this.movie = movie;
		this.theater = theater;
		this.startsAt = startsAt;
		this.basePrice = basePrice;
		this.premiumSurcharge = premiumSurcharge;
		this.weekendMultiplier = weekendMultiplier;
		this.holdMinutes = holdMinutes;
	}

	public Long getId() {
		return id;
	}

	public Movie getMovie() {
		return movie;
	}

	public Theater getTheater() {
		return theater;
	}

	public LocalDateTime getStartsAt() {
		return startsAt;
	}

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public BigDecimal getPremiumSurcharge() {
		return premiumSurcharge;
	}

	public BigDecimal getWeekendMultiplier() {
		return weekendMultiplier;
	}

	public int getHoldMinutes() {
		return holdMinutes;
	}
}
