package com.busticket.backend.controller;

import com.busticket.backend.dto.BookingRequestDTO;
import com.busticket.backend.dto.BookingResponseDTO;
import com.busticket.backend.dto.SeatDTO;
import com.busticket.backend.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/trips/{tripId}/seats")
    public ResponseEntity<List<SeatDTO>> getSeatsForTrip(@PathVariable Long tripId) {
        return ResponseEntity.ok(bookingService.getSeatsForTrip(tripId));
    }

    @PostMapping("/bookings")
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingRequestDTO request) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    @GetMapping("/users/{userId}/bookings")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsForUser(userId));
    }
}
