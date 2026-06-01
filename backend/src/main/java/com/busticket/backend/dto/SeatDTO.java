package com.busticket.backend.dto;

import lombok.Data;

@Data
public class SeatDTO {
    private Long id;
    private String seatNumber;
    private Boolean isBooked;
}
