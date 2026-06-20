package com.dmg.MovieTicketBookingSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dmg.MovieTicketBookingSystem.domain.City;

public interface CityRepository extends JpaRepository<City, Long> {
}
