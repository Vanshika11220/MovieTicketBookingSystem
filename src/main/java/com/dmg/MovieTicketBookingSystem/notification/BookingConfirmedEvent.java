package com.dmg.MovieTicketBookingSystem.notification;

public record BookingConfirmedEvent(Long bookingId, String bookingReference) {
}
