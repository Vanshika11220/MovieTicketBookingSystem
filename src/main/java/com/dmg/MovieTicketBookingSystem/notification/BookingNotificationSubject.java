package com.dmg.MovieTicketBookingSystem.notification;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.dmg.MovieTicketBookingSystem.domain.enums.NotificationType;

@Component
public class BookingNotificationSubject {
	private final List<NotificationObserver> observers;

	public BookingNotificationSubject(List<NotificationObserver> observers) {
		this.observers = observers;
	}

	@Async
	public void bookingConfirmed(Long bookingId, String bookingReference) {
		notifyObservers(new NotificationMessage(NotificationType.BOOKING_CONFIRMED, bookingId, bookingReference));
	}

	@Async
	public void bookingCancelled(Long bookingId, String bookingReference) {
		notifyObservers(new NotificationMessage(NotificationType.BOOKING_CANCELLED, bookingId, bookingReference));
	}

	private void notifyObservers(NotificationMessage message) {
		observers.forEach(observer -> observer.update(message));
	}
}
