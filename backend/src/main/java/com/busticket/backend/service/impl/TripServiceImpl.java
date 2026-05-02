package com.busticket.backend.service.impl;

import com.busticket.backend.dto.TripDTO;
import com.busticket.backend.entity.Trip;
import com.busticket.backend.mapper.TripMapper;
import com.busticket.backend.repository.BookingRepository;
import com.busticket.backend.repository.TripRepository;
import com.busticket.backend.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final BookingRepository bookingRepository;
    private final TripMapper tripMapper;

    @Override
    public List<TripDTO> searchTrips(Long fromLocationId, Long toLocationId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Trip> trips = tripRepository.findAvailableTrips(fromLocationId, toLocationId, startOfDay, endOfDay);

        return trips.stream().map(trip -> {
            TripDTO dto = tripMapper.toDto(trip);
            
            // FIXME: Here we need to properly count booked seats by querying BookingSeat
            // For now, we mock the available seats logic
            int totalSeats = trip.getBus().getTotalSeats();
            int mockBookedSeats = 0; // We will implement proper booked seats count later
            dto.setAvailableSeats(totalSeats - mockBookedSeats);
            
            return dto;
        }).collect(Collectors.toList());
    }
}
