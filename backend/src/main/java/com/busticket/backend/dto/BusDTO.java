package com.busticket.backend.dto;

import lombok.Data;

@Data
public class BusDTO {
    private Long id;
    private String licensePlate;
    private String type;
    private Integer totalSeats;
}
