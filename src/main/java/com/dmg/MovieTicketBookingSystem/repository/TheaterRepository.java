package com.dmg.MovieTicketBookingSystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dmg.MovieTicketBookingSystem.domain.Theater;

public interface TheaterRepository extends JpaRepository<Theater, Long> {
	List<Theater> findByCityId(Long cityId);
}
