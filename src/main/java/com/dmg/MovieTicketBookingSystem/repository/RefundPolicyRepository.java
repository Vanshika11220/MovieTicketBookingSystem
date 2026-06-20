package com.dmg.MovieTicketBookingSystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dmg.MovieTicketBookingSystem.domain.RefundPolicy;

public interface RefundPolicyRepository extends JpaRepository<RefundPolicy, Long> {
	Optional<RefundPolicy> findFirstByActiveDefaultTrue();
}
