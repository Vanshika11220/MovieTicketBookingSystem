package com.dmg.MovieTicketBookingSystem.common;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException {
	public NotFoundException(String resource, Long id) {
		super(HttpStatus.NOT_FOUND, resource + " not found: " + id);
	}
}
