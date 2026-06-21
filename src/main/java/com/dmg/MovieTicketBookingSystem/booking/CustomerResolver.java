package com.dmg.MovieTicketBookingSystem.booking;

import org.springframework.stereotype.Component;

import com.dmg.MovieTicketBookingSystem.common.NotFoundException;
import com.dmg.MovieTicketBookingSystem.domain.UserAccount;
import com.dmg.MovieTicketBookingSystem.repository.UserAccountRepository;
import com.dmg.MovieTicketBookingSystem.security.UserContext;

@Component
public class CustomerResolver {
	private final UserAccountRepository userAccountRepository;
	private final UserContext userContext;

	public CustomerResolver(UserAccountRepository userAccountRepository, UserContext userContext) {
		this.userAccountRepository = userAccountRepository;
		this.userContext = userContext;
	}

	public UserAccount currentCustomer() {
		Long userId = userContext.currentUser().id();
		return userAccountRepository.findById(userId).orElseThrow(() -> new NotFoundException("User", userId));
	}
}
