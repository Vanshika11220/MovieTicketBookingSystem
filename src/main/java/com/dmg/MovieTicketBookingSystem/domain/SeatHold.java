package com.dmg.MovieTicketBookingSystem.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class SeatHold {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private MovieShow movieShow;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Seat seat;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private UserAccount customer;

	@Column(nullable = false)
	private LocalDateTime expiresAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private HoldStatus status;

	protected SeatHold() {
	}

	public SeatHold(MovieShow movieShow, Seat seat, UserAccount customer, LocalDateTime expiresAt) {
		this.movieShow = movieShow;
		this.seat = seat;
		this.customer = customer;
		this.expiresAt = expiresAt;
		this.status = HoldStatus.ACTIVE;
	}

	public Long getId() {
		return id;
	}

	public MovieShow getMovieShow() {
		return movieShow;
	}

	public Seat getSeat() {
		return seat;
	}

	public UserAccount getCustomer() {
		return customer;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public HoldStatus getStatus() {
		return status;
	}

	public void markConfirmed() {
		this.status = HoldStatus.CONFIRMED;
	}

	public void markExpired() {
		this.status = HoldStatus.EXPIRED;
	}
}
