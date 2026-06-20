package com.dmg.MovieTicketBookingSystem.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dmg.MovieTicketBookingSystem.domain.Seat;

import jakarta.persistence.LockModeType;

public interface SeatRepository extends JpaRepository<Seat, Long> {
	List<Seat> findByTheaterIdOrderByRowLabelAscSeatNumberAsc(Long theaterId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select s from Seat s where s.id in :ids order by s.id")
	List<Seat> lockAllByIdIn(@Param("ids") Collection<Long> ids);
}
