package com.dmg.MovieTicketBookingSystem.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dmg.MovieTicketBookingSystem.domain.MovieShow;

public interface MovieShowRepository extends JpaRepository<MovieShow, Long> {
	@Query("""
			select ms from MovieShow ms
			join fetch ms.movie m
			join fetch ms.theater t
			join fetch t.city c
			where (:cityId is null or c.id = :cityId)
			  and ms.startsAt >= :from
			order by ms.startsAt
			""")
	List<MovieShow> browse(@Param("cityId") Long cityId, @Param("from") LocalDateTime from);
}
