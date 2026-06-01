package com.busticket.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VnpayPaymentRequestDTO {
    @NotNull
    private Long bookingId;

    private String bankCode;

    private String locale = "vn";
}
