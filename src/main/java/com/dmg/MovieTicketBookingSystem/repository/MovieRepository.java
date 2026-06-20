package com.dmg.MovieTicketBookingSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dmg.MovieTicketBookingSystem.domain.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}
