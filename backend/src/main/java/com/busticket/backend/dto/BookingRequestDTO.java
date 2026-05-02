package com.busticket.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class BookingRequestDTO {
    @NotNull(message = "User ID is required")
    private Long userId; // TODO: Extract from JWT token later

    @NotNull(message = "Trip ID is required")
    private Long tripId;

    @NotEmpty(message = "At least one seat must be selected")
    private List<Long> seatIds;
}
