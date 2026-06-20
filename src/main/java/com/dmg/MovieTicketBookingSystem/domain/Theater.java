package com.dmg.MovieTicketBookingSystem.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Theater {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String address;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private City city;

	protected Theater() {
	}

	public Theater(String name, String address, City city) {
		this.name = name;
		this.address = address;
		this.city = city;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public City getCity() {
		return city;
	}
}
