package com.dmg.MovieTicketBookingSystem.booking;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.CancelBookingResponse;
import com.dmg.MovieTicketBookingSystem.common.ApiException;
import com.dmg.MovieTicketBookingSystem.common.NotFoundException;
import com.dmg.MovieTicketBookingSystem.domain.Booking;
import com.dmg.MovieTicketBookingSystem.domain.UserAccount;
import com.dmg.MovieTicketBookingSystem.domain.enums.BookingStatus;
import com.dmg.MovieTicketBookingSystem.notification.BookingNotificationSubject;
import com.dmg.MovieTicketBookingSystem.refund.RefundCalculator;
import com.dmg.MovieTicketBookingSystem.repository.BookingRepository;

@Service
public class BookingCancellationService {
	private final BookingRepository bookingRepository;
	private final CustomerResolver customerResolver;
	private final RefundCalculator refundCalculator;
	private final BookingNotificationSubject notificationSubject;

	public BookingCancellationService(BookingRepository bookingRepository, CustomerResolver customerResolver,
			RefundCalculator refundCalculator, BookingNotificationSubject notificationSubject) {
		this.bookingRepository = bookingRepository;
		this.customerResolver = customerResolver;
		this.refundCalculator = refundCalculator;
		this.notificationSubject = notificationSubject;
	}

	@Transactional
	public CancelBookingResponse cancelBooking(Long bookingId) {
		UserAccount customer = customerResolver.currentCustomer();
		Booking booking = bookingRepository.findDetailedById(bookingId)
				.orElseThrow(() -> new NotFoundException("Booking", bookingId));
		if (!booking.getCustomer().getId().equals(customer.getId())) {
			throw new ApiException(HttpStatus.FORBIDDEN, "Cannot cancel another customer's booking");
		}
		if (booking.getStatus() == BookingStatus.CANCELLED) {
			throw new ApiException(HttpStatus.CONFLICT, "Booking is already cancelled");
		}
		var refund = refundCalculator.calculateRefund(booking);
		booking.cancel(refund);
		notificationSubject.bookingCancelled(booking.getId(), booking.getBookingReference());
		return new CancelBookingResponse(booking.getId(), booking.getStatus(), booking.getRefundAmount());
	}
}
