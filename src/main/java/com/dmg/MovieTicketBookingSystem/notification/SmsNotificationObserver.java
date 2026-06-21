package com.dmg.MovieTicketBookingSystem.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SmsNotificationObserver implements NotificationObserver {
	private static final Logger LOGGER = LoggerFactory.getLogger(SmsNotificationObserver.class);

	@Override
	public void update(NotificationMessage message) {
		LOGGER.info("SMS notification queued for {} booking {}", message.type(), message.bookingReference());
	}
}
