package com.busticket.backend.service;

import com.busticket.backend.dto.PaymentStatusResponseDTO;
import com.busticket.backend.dto.VnpayPaymentRequestDTO;
import com.busticket.backend.dto.VnpayPaymentResponseDTO;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

public interface VnpayPaymentService {
    VnpayPaymentResponseDTO createPaymentUrl(VnpayPaymentRequestDTO request, HttpServletRequest httpRequest);

    Map<String, String> handleIpn(Map<String, String> params);

    String handleReturn(Map<String, String> params);

    PaymentStatusResponseDTO getPaymentStatus(Long bookingId);
}
