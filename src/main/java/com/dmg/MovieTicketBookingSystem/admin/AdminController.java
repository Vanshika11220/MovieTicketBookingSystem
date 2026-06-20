package com.dmg.MovieTicketBookingSystem.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.dmg.MovieTicketBookingSystem.admin.AdminDtos.CreateCityRequest;
import com.dmg.MovieTicketBookingSystem.admin.AdminDtos.CreateDiscountRequest;
import com.dmg.MovieTicketBookingSystem.admin.AdminDtos.CreateMovieRequest;
import com.dmg.MovieTicketBookingSystem.admin.AdminDtos.CreateRefundPolicyRequest;
import com.dmg.MovieTicketBookingSystem.admin.AdminDtos.CreateSeatLayoutRequest;
import com.dmg.MovieTicketBookingSystem.admin.AdminDtos.CreateShowRequest;
import com.dmg.MovieTicketBookingSystem.admin.AdminDtos.CreateTheaterRequest;
import com.dmg.MovieTicketBookingSystem.admin.AdminDtos.IdResponse;
import com.dmg.MovieTicketBookingSystem.domain.Role;
import com.dmg.MovieTicketBookingSystem.security.RoleRequired;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@RoleRequired(Role.ADMIN)
public class AdminController {
	private final AdminService adminService;

	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}

	@PostMapping("/cities")
	@ResponseStatus(HttpStatus.CREATED)
	public IdResponse createCity(@Valid @RequestBody CreateCityRequest request) {
		return new IdResponse(adminService.createCity(request));
	}

	@PostMapping("/theaters")
	@ResponseStatus(HttpStatus.CREATED)
	public IdResponse createTheater(@Valid @RequestBody CreateTheaterRequest request) {
		return new IdResponse(adminService.createTheater(request));
	}

	@PostMapping("/seat-layouts")
	@ResponseStatus(HttpStatus.CREATED)
	public List<Long> createSeatLayout(@Valid @RequestBody CreateSeatLayoutRequest request) {
		return adminService.createSeatLayout(request);
	}

	@PostMapping("/movies")
	@ResponseStatus(HttpStatus.CREATED)
	public IdResponse createMovie(@Valid @RequestBody CreateMovieRequest request) {
		return new IdResponse(adminService.createMovie(request));
	}

	@PostMapping("/shows")
	@ResponseStatus(HttpStatus.CREATED)
	public IdResponse createShow(@Valid @RequestBody CreateShowRequest request) {
		return new IdResponse(adminService.createShow(request));
	}

	@PostMapping("/discounts")
	@ResponseStatus(HttpStatus.CREATED)
	public IdResponse createDiscount(@Valid @RequestBody CreateDiscountRequest request) {
		return new IdResponse(adminService.createDiscount(request));
	}

	@PostMapping("/refund-policies")
	@ResponseStatus(HttpStatus.CREATED)
	public IdResponse createRefundPolicy(@Valid @RequestBody CreateRefundPolicyRequest request) {
		return new IdResponse(adminService.createRefundPolicy(request));
	}
}
