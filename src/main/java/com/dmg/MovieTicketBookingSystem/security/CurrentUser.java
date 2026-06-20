package com.dmg.MovieTicketBookingSystem.security;

import com.dmg.MovieTicketBookingSystem.domain.Role;

public record CurrentUser(Long id, Role role) {
}
