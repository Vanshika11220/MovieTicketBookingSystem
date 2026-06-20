package com.dmg.MovieTicketBookingSystem.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dmg.MovieTicketBookingSystem.domain.HoldStatus;
import com.dmg.MovieTicketBookingSystem.domain.SeatHold;

public interface SeatHoldRepository extends JpaRepository<SeatHold, Long> {
	@Query("""
			select h from SeatHold h
			join fetch h.seat
			join fetch h.customer
			where h.movieShow.id = :showId
			  and h.seat.id in :seatIds
			  and h.status = :status
			  and h.expiresAt > :now
			""")
	List<SeatHold> findActiveHolds(@Param("showId") Long showId, @Param("seatIds") Collection<Long> seatIds,
			@Param("status") HoldStatus status, @Param("now") LocalDateTime now);

	@Query("""
			select h from SeatHold h
			join fetch h.seat
			join fetch h.customer
			where h.id in :holdIds and h.status = :status
			""")
	List<SeatHold> findActiveByIds(@Param("holdIds") Collection<Long> holdIds, @Param("status") HoldStatus status);

	@Modifying
	@Query("update SeatHold h set h.status = 'EXPIRED' where h.status = 'ACTIVE' and h.expiresAt <= :now")
	int expireOldHolds(@Param("now") LocalDateTime now);
}
