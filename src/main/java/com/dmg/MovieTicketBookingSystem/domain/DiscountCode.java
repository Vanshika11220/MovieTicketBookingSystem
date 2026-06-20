package com.dmg.MovieTicketBookingSystem.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class DiscountCode {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String code;

	@Column(nullable = false)
	private BigDecimal percentage;

	private LocalDate validUntil;
	private boolean active;

	protected DiscountCode() {
	}

	public DiscountCode(String code, BigDecimal percentage, LocalDate validUntil, boolean active) {
		this.code = code;
		this.percentage = percentage;
		this.validUntil = validUntil;
		this.active = active;
	}

	public Long getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public BigDecimal getPercentage() {
		return percentage;
	}

	public LocalDate getValidUntil() {
		return validUntil;
	}

	public boolean isActive() {
		return active;
	}
}
