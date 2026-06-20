package com.dmg.MovieTicketBookingSystem.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class BookingSeat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Booking booking;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Seat seat;

	protected BookingSeat() {
	}

	public BookingSeat(Booking booking, Seat seat) {
		this.booking = booking;
		this.seat = seat;
	}

	public Seat getSeat() {
		return seat;
	}
}
