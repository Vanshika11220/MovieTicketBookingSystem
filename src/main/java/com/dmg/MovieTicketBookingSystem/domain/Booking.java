package com.dmg.MovieTicketBookingSystem.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Booking {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String bookingReference;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private MovieShow movieShow;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private UserAccount customer;

	@OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
	private Payment payment;

	@OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BookingSeat> seats = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BookingStatus status;

	@Column(nullable = false)
	private BigDecimal amountPaid;

	private BigDecimal refundAmount = BigDecimal.ZERO;
	private LocalDateTime confirmedAt;
	private LocalDateTime cancelledAt;
	private String discountCode;

	protected Booking() {
	}

	public Booking(MovieShow movieShow, UserAccount customer, Payment payment, BigDecimal amountPaid, String discountCode) {
		this.bookingReference = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
		this.movieShow = movieShow;
		this.customer = customer;
		this.payment = payment;
		this.amountPaid = amountPaid;
		this.discountCode = discountCode;
		this.status = BookingStatus.CONFIRMED;
		this.confirmedAt = LocalDateTime.now();
	}

	public void addSeat(Seat seat) {
		seats.add(new BookingSeat(this, seat));
	}

	public void cancel(BigDecimal refundAmount) {
		this.status = BookingStatus.CANCELLED;
		this.refundAmount = refundAmount;
		this.cancelledAt = LocalDateTime.now();
		this.payment.markRefunded();
	}

	public Long getId() {
		return id;
	}

	public String getBookingReference() {
		return bookingReference;
	}

	public MovieShow getMovieShow() {
		return movieShow;
	}

	public UserAccount getCustomer() {
		return customer;
	}

	public List<BookingSeat> getSeats() {
		return seats;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public BigDecimal getAmountPaid() {
		return amountPaid;
	}

	public BigDecimal getRefundAmount() {
		return refundAmount;
	}

	public LocalDateTime getConfirmedAt() {
		return confirmedAt;
	}
}
