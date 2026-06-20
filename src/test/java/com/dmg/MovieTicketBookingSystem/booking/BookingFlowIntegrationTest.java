package com.dmg.MovieTicketBookingSystem.booking;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.dmg.MovieTicketBookingSystem.domain.MovieShow;
import com.dmg.MovieTicketBookingSystem.domain.Seat;
import com.dmg.MovieTicketBookingSystem.repository.MovieShowRepository;
import com.dmg.MovieTicketBookingSystem.repository.SeatRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class BookingFlowIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MovieShowRepository movieShowRepository;

	@Autowired
	private SeatRepository seatRepository;

	@Test
	void customerCanHoldConfirmCancelAndViewHistory() throws Exception {
		TestFixture fixture = fixture();

		String holdJson = mockMvc.perform(post("/api/customer/holds")
						.header("X-User-Id", "2")
						.header("X-User-Role", "CUSTOMER")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of(
								"showId", fixture.showId(),
								"seatIds", List.of(fixture.seatIds().get(0), fixture.seatIds().get(1))))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.holdIds", hasSize(2)))
				.andReturn().getResponse().getContentAsString();

		JsonNode hold = objectMapper.readTree(holdJson);
		List<Long> holdIds = List.of(hold.get("holdIds").get(0).asLong(), hold.get("holdIds").get(1).asLong());

		String bookingJson = mockMvc.perform(post("/api/customer/bookings")
						.header("X-User-Id", "2")
						.header("X-User-Role", "CUSTOMER")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of(
								"holdIds", holdIds,
								"discountCode", "WELCOME10"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("CONFIRMED"))
				.andExpect(jsonPath("$.seats", hasSize(2)))
				.andReturn().getResponse().getContentAsString();

		long bookingId = objectMapper.readTree(bookingJson).get("bookingId").asLong();

		mockMvc.perform(get("/api/customer/bookings")
						.header("X-User-Id", "2")
						.header("X-User-Role", "CUSTOMER"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].bookingId").value(bookingId));

		mockMvc.perform(post("/api/customer/bookings/{bookingId}/cancel", bookingId)
						.header("X-User-Id", "2")
						.header("X-User-Role", "CUSTOMER"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("CANCELLED"))
				.andExpect(jsonPath("$.refundAmount").value(450.00));
	}

	@Test
	void heldSeatCannotBeHeldByAnotherCustomer() throws Exception {
		TestFixture fixture = fixture();
		Long seatId = fixture.seatIds().get(2);

		mockMvc.perform(post("/api/customer/holds")
						.header("X-User-Id", "2")
						.header("X-User-Role", "CUSTOMER")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of("showId", fixture.showId(), "seatIds", List.of(seatId)))))
				.andExpect(status().isOk());

		mockMvc.perform(post("/api/customer/holds")
						.header("X-User-Id", "3")
						.header("X-User-Role", "CUSTOMER")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of("showId", fixture.showId(), "seatIds", List.of(seatId)))))
				.andExpect(status().isConflict());
	}

	@Test
	void adminEndpointsRejectCustomerRole() throws Exception {
		mockMvc.perform(post("/api/admin/cities")
						.header("X-User-Id", "2")
						.header("X-User-Role", "CUSTOMER")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of("name", "Mumbai"))))
				.andExpect(status().isForbidden());
	}

	private TestFixture fixture() {
		MovieShow show = movieShowRepository.browse(null, LocalDateTime.now()).get(0);
		List<Long> seatIds = seatRepository.findByTheaterIdOrderByRowLabelAscSeatNumberAsc(show.getTheater().getId())
				.stream().map(Seat::getId).toList();
		return new TestFixture(show.getId(), seatIds);
	}

	private record TestFixture(Long showId, List<Long> seatIds) {
	}
}
