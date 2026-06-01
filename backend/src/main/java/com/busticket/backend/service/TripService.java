package com.busticket.backend.service;

import com.busticket.backend.dto.TripDTO;

import java.time.LocalDate;
import java.util.List;

public interface TripService {
    List<TripDTO> searchTrips(Long fromLocationId, Long toLocationId, LocalDate date);
}
