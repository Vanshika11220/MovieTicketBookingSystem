package com.dmg.MovieTicketBookingSystem.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.dmg.MovieTicketBookingSystem.domain.enums.SeatType;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"theater_id", "rowLabel", "seatNumber"}))
public class Seat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Theater theater;

	@Column(nullable = false)
	private String rowLabel;

	@Column(nullable = false)
	private int seatNumber;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SeatType seatType;

	protected Seat() {
	}

	public Seat(Theater theater, String rowLabel, int seatNumber, SeatType seatType) {
		this.theater = theater;
		this.rowLabel = rowLabel;
		this.seatNumber = seatNumber;
		this.seatType = seatType;
	}

	public Long getId() {
		return id;
	}

	public Theater getTheater() {
		return theater;
	}

	public String getRowLabel() {
		return rowLabel;
	}

	public int getSeatNumber() {
		return seatNumber;
	}

	public SeatType getSeatType() {
		return seatType;
	}

	public String label() {
		return rowLabel + seatNumber;
	}
}
