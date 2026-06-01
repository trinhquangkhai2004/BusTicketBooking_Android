package com.busticket.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class VnpayPaymentResponseDTO {
    private Long bookingId;
    private Long paymentId;
    private String paymentUrl;
    private BigDecimal amount;
    private LocalDateTime expiresAt;
}
