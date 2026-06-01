package com.busticket.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingResponseDTO {
    private Long id;
    private String tripRoute;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String duration;
    private String licensePlate;
    private List<String> seatNumbers;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
}
