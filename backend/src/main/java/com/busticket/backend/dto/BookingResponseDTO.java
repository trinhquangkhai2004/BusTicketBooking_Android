package com.busticket.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingResponseDTO {
    private Long id;
    private String tripRoute;
    private LocalDateTime departureTime;
    private BigDecimal totalAmount;
    private String status;
}
