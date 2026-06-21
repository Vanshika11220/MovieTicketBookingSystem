package com.dmg.MovieTicketBookingSystem.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;

import com.dmg.MovieTicketBookingSystem.notification.NotificationObserver;
import com.dmg.MovieTicketBookingSystem.payment.PaymentStrategy;
import com.dmg.MovieTicketBookingSystem.pricing.PricingModifier;
import com.dmg.MovieTicketBookingSystem.pricing.SeatPricingStrategy;

/**
 * Documents how Strategy and Observer implementations are wired.
 * Spring auto-discovers {@code @Component} beans and injects them as {@code List<>}
 * into {@code PaymentProcessor}, {@code DefaultPricingPolicy}, and
 * {@code BookingNotificationSubject}.
 */
@Configuration
public class StrategyConfiguration {

	public StrategyConfiguration(List<PaymentStrategy> paymentStrategies,
			List<SeatPricingStrategy> seatPricingStrategies, List<PricingModifier> pricingModifiers,
			List<NotificationObserver> notificationObservers) {
		// Constructor injection validates that all strategy/observer beans are present at startup.
	}
}
