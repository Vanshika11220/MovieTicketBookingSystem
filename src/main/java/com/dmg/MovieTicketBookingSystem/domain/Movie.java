package com.dmg.MovieTicketBookingSystem.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Movie {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	private int durationMinutes;
	private String language;

	protected Movie() {
	}

	public Movie(String title, int durationMinutes, String language) {
		this.title = title;
		this.durationMinutes = durationMinutes;
		this.language = language;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public int getDurationMinutes() {
		return durationMinutes;
	}

	public String getLanguage() {
		return language;
	}
}
