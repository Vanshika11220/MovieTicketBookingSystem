package com.dmg.MovieTicketBookingSystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dmg.MovieTicketBookingSystem.domain.DiscountCode;

public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Long> {
	Optional<DiscountCode> findByCodeIgnoreCase(String code);
}
