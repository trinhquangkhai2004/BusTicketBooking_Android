package com.busticket.backend.service;

import com.busticket.backend.dto.BookingRequestDTO;
import com.busticket.backend.dto.BookingResponseDTO;
import com.busticket.backend.dto.SeatDTO;

import java.util.List;

public interface BookingService {
    BookingResponseDTO createBooking(BookingRequestDTO request);
    List<BookingResponseDTO> getBookingsForUser(Long userId);
    List<SeatDTO> getSeatsForTrip(Long tripId);
}
