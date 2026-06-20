package com.dmg.MovieTicketBookingSystem.notification;

public record BookingCancelledEvent(Long bookingId, String bookingReference) {
}
