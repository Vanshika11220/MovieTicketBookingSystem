package com.dmg.MovieTicketBookingSystem.notification;

import com.dmg.MovieTicketBookingSystem.domain.enums.NotificationType;

public record NotificationMessage(NotificationType type, Long bookingId, String bookingReference) {
}
