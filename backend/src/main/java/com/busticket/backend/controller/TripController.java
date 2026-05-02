package com.busticket.backend.controller;

import com.busticket.backend.dto.TripDTO;
import com.busticket.backend.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @GetMapping("/search")
    public ResponseEntity<List<TripDTO>> searchTrips(
            @RequestParam Long from,
            @RequestParam Long to,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<TripDTO> trips = tripService.searchTrips(from, to, date);
        return ResponseEntity.ok(trips);
    }
}
