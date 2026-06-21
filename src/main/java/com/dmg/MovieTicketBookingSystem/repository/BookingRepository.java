package com.dmg.MovieTicketBookingSystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dmg.MovieTicketBookingSystem.domain.Booking;
import com.dmg.MovieTicketBookingSystem.domain.enums.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	List<Booking> findByCustomerIdOrderByConfirmedAtDesc(Long customerId);

	@Query("""
			select b from Booking b
			join fetch b.seats bs
			join fetch bs.seat
			join fetch b.movieShow ms
			join fetch ms.movie
			where b.id = :id
			""")
	Optional<Booking> findDetailedById(@Param("id") Long id);

	@Query("""
			select count(bs) > 0 from BookingSeat bs
			where bs.booking.movieShow.id = :showId
			  and bs.seat.id in :seatIds
			  and bs.booking.status = :status
			""")
	boolean anyConfirmedSeat(@Param("showId") Long showId, @Param("seatIds") List<Long> seatIds,
			@Param("status") BookingStatus status);

	@Query("""
			select bs.seat.id from BookingSeat bs
			where bs.booking.movieShow.id = :showId
			  and bs.seat.id in :seatIds
			  and bs.booking.status = :status
			""")
	List<Long> findConfirmedSeatIds(@Param("showId") Long showId, @Param("seatIds") List<Long> seatIds,
			@Param("status") BookingStatus status);
}
