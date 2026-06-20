package com.dmg.MovieTicketBookingSystem.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class RefundPolicy {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	private long cutoffMinutesBeforeShow;

	@Column(nullable = false)
	private BigDecimal refundPercentage;

	private boolean activeDefault;

	protected RefundPolicy() {
	}

	public RefundPolicy(String name, long cutoffMinutesBeforeShow, BigDecimal refundPercentage, boolean activeDefault) {
		this.name = name;
		this.cutoffMinutesBeforeShow = cutoffMinutesBeforeShow;
		this.refundPercentage = refundPercentage;
		this.activeDefault = activeDefault;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public long getCutoffMinutesBeforeShow() {
		return cutoffMinutesBeforeShow;
	}

	public BigDecimal getRefundPercentage() {
		return refundPercentage;
	}

	public boolean isActiveDefault() {
		return activeDefault;
	}
}
