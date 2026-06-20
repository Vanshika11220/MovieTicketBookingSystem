package com.dmg.MovieTicketBookingSystem.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationListener.class);

	@Async
	@EventListener
	public void onBookingConfirmed(BookingConfirmedEvent event) {
		LOGGER.info("Queued confirmation and reminder notification for booking {}", event.bookingReference());
	}

	@Async
	@EventListener
	public void onBookingCancelled(BookingCancelledEvent event) {
		LOGGER.info("Queued cancellation notification for booking {}", event.bookingReference());
	}
}
