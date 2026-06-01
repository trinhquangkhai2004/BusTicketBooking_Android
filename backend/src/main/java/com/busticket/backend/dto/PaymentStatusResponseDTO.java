package com.busticket.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentStatusResponseDTO {
    private Long bookingId;
    private String bookingStatus;
    private String paymentStatus;
    private boolean paid;
}
