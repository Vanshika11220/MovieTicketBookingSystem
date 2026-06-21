package com.dmg.MovieTicketBookingSystem.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import com.dmg.MovieTicketBookingSystem.domain.enums.PaymentStatus;

@Entity
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String providerReference;

	@Column(nullable = false)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus status;

	private LocalDateTime paidAt;

	protected Payment() {
	}

	public Payment(String providerReference, BigDecimal amount) {
		this.providerReference = providerReference;
		this.amount = amount;
		this.status = PaymentStatus.SUCCESS;
		this.paidAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public String getProviderReference() {
		return providerReference;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public void markRefunded() {
		this.status = PaymentStatus.REFUNDED;
	}
}
