package com.busticket.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TripDTO {
    private Long id;
    private LocationDTO departureLocation;
    private LocationDTO arrivalLocation;
    private BusDTO bus;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal price;
    private String duration; // From Route
    private Integer availableSeats;
}
