package com.dmg.MovieTicketBookingSystem.booking;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.BookingResponse;
import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.CancelBookingResponse;
import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.ConfirmBookingRequest;
import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.HoldSeatsRequest;
import com.dmg.MovieTicketBookingSystem.booking.BookingDtos.HoldSeatsResponse;
import com.dmg.MovieTicketBookingSystem.domain.UserAccount;
import com.dmg.MovieTicketBookingSystem.repository.BookingRepository;

@Service
public class BookingQueryService {
	private final BookingRepository bookingRepository;
	private final CustomerResolver customerResolver;
	private final BookingMapper bookingMapper;

	public BookingQueryService(BookingRepository bookingRepository, CustomerResolver customerResolver,
			BookingMapper bookingMapper) {
		this.bookingRepository = bookingRepository;
		this.customerResolver = customerResolver;
		this.bookingMapper = bookingMapper;
	}

	@Transactional(readOnly = true)
	public List<BookingResponse> bookingHistory() {
		UserAccount customer = customerResolver.currentCustomer();
		return bookingRepository.findByCustomerIdOrderByConfirmedAtDesc(customer.getId()).stream()
				.map(bookingMapper::toResponse)
				.toList();
	}
}
