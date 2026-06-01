package com.busticket.backend.controller;

import com.busticket.backend.dto.PaymentStatusResponseDTO;
import com.busticket.backend.dto.VnpayPaymentRequestDTO;
import com.busticket.backend.dto.VnpayPaymentResponseDTO;
import com.busticket.backend.service.VnpayPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final VnpayPaymentService vnpayPaymentService;

    @PostMapping("/vnpay")
    public ResponseEntity<VnpayPaymentResponseDTO> createVnpayPayment(
            @Valid @RequestBody VnpayPaymentRequestDTO request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(vnpayPaymentService.createPaymentUrl(request, httpRequest));
    }

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<Map<String, String>> vnpayIpn(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(vnpayPaymentService.handleIpn(params));
    }

    @GetMapping(value = "/vnpay-return", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> vnpayReturn(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(vnpayPaymentService.handleReturn(params));
    }

    @GetMapping("/bookings/{bookingId}/status")
    public ResponseEntity<PaymentStatusResponseDTO> getPaymentStatus(@PathVariable Long bookingId) {
        return ResponseEntity.ok(vnpayPaymentService.getPaymentStatus(bookingId));
    }
}
