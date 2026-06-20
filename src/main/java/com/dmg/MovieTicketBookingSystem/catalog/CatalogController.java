package com.dmg.MovieTicketBookingSystem.catalog;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dmg.MovieTicketBookingSystem.catalog.CatalogDtos.ShowResponse;
import com.dmg.MovieTicketBookingSystem.catalog.CatalogDtos.ShowSeatsResponse;

@RestController
@RequestMapping("/api")
public class CatalogController {
	private final CatalogService catalogService;

	public CatalogController(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	@GetMapping("/shows")
	public List<ShowResponse> browseShows(@RequestParam(required = false) Long cityId) {
		return catalogService.browseShows(cityId);
	}

	@GetMapping("/shows/{showId}/seats")
	public ShowSeatsResponse seatsForShow(@PathVariable Long showId) {
		return catalogService.seatsForShow(showId);
	}
}
