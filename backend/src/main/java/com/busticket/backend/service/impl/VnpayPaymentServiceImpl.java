package com.busticket.backend.service.impl;

import com.busticket.backend.config.VnpayProperties;
import com.busticket.backend.dto.PaymentStatusResponseDTO;
import com.busticket.backend.dto.VnpayPaymentRequestDTO;
import com.busticket.backend.dto.VnpayPaymentResponseDTO;
import com.busticket.backend.entity.Booking;
import com.busticket.backend.entity.Payment;
import com.busticket.backend.exception.BusinessException;
import com.busticket.backend.exception.ResourceNotFoundException;
import com.busticket.backend.repository.BookingRepository;
import com.busticket.backend.repository.PaymentRepository;
import com.busticket.backend.service.EmailNotificationProducer;
import com.busticket.backend.service.VnpayPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class VnpayPaymentServiceImpl implements VnpayPaymentService {
    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter VNPAY_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final VnpayProperties properties;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final EmailNotificationProducer emailNotificationProducer;

    @Override
    @Transactional
    public VnpayPaymentResponseDTO createPaymentUrl(VnpayPaymentRequestDTO request, HttpServletRequest httpRequest) {
        validateConfig();

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new BusinessException("Booking đã bị huỷ, không thể thanh toán.");
        }
        if (booking.getStatus() == Booking.BookingStatus.CONFIRMED) {
            throw new BusinessException("Booking đã được thanh toán.");
        }

        Payment payment = paymentRepository.findByBookingId(booking.getId())
                .orElseGet(() -> Payment.builder()
                        .booking(booking)
                        .method(Payment.PaymentMethod.VNPAY)
                        .status(Payment.PaymentStatus.PENDING)
                        .amount(booking.getTotalAmount())
                        .build());

        if (payment.getStatus() == Payment.PaymentStatus.SUCCESS) {
            throw new BusinessException("Giao dịch đã thanh toán thành công.");
        }

        LocalDateTime now = LocalDateTime.now(VN_ZONE);
        LocalDateTime expiresAt = now.plusMinutes(properties.getExpireMinutes());
        String txnRef = booking.getId() + "-" + System.currentTimeMillis();

        payment.setMethod(Payment.PaymentMethod.VNPAY);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setAmount(booking.getTotalAmount());
        payment.setGatewayTxnRef(txnRef);
        payment.setResponseCode(null);
        payment.setTransactionStatus(null);
        payment.setGatewayTransactionNo(null);
        payment.setBankCode(null);
        payment.setCardType(null);
        payment = paymentRepository.save(payment);

        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", properties.getTmnCode());
        params.put("vnp_Amount", toVnpayAmount(booking.getTotalAmount()));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", "Thanh toan ve xe booking " + booking.getId());
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", normalizeLocale(request.getLocale()));
        params.put("vnp_ReturnUrl", properties.getReturnUrl());
        params.put("vnp_IpAddr", getClientIp(httpRequest));
        params.put("vnp_CreateDate", now.format(VNPAY_TIME_FORMAT));
        params.put("vnp_ExpireDate", expiresAt.format(VNPAY_TIME_FORMAT));
        if (StringUtils.hasText(request.getBankCode())) {
            params.put("vnp_BankCode", request.getBankCode().trim());
        }

        String query = buildQuery(params);
        String secureHash = hmacSha512(properties.getEffectiveHashSecret(), query);
        String paymentUrl = properties.getPaymentUrl() + "?" + query + "&vnp_SecureHash=" + secureHash;

        return VnpayPaymentResponseDTO.builder()
                .bookingId(booking.getId())
                .paymentId(payment.getId())
                .paymentUrl(paymentUrl)
                .amount(payment.getAmount())
                .expiresAt(expiresAt)
                .build();
    }

    @Override
    @Transactional
    public Map<String, String> handleIpn(Map<String, String> params) {
        CallbackResult result = processCallback(params);
        Map<String, String> response = new LinkedHashMap<>();
        response.put("RspCode", result.code());
        response.put("Message", result.message());
        return response;
    }

    @SuppressWarnings("unused")
    private String renderLegacyReturnPage(Map<String, String> params) {
        CallbackResult result = processCallback(params);
        boolean success = "00".equals(params.get("vnp_ResponseCode"))
                && ("00".equals(result.code()) || "02".equals(result.code()));
        String title = success ? "Thanh toán thành công" : "Thanh toán chưa hoàn tất";
        String message = success
                ? "Bạn có thể quay lại ứng dụng để xem vé."
                : "Giao dịch không thành công hoặc chưa được xác nhận. Vui lòng quay lại ứng dụng.";
        return """
                <!doctype html>
                <html lang="vi">
                <head>
                    <meta charset="utf-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <title>%s</title>
                    <style>
                        body { font-family: sans-serif; padding: 32px; text-align: center; color: #1f2937; }
                        .status { font-size: 20px; font-weight: 700; margin-bottom: 12px; }
                        .message { color: #6b7280; line-height: 1.5; }
                    </style>
                </head>
                <body>
                    <div class="status">%s</div>
                    <div class="message">%s</div>
                </body>
                </html>
                """.formatted(title, title, message);
    }

    @Override
    @Transactional
    public String handleReturn(Map<String, String> params) {
        CallbackResult result = processCallback(params);
        boolean success = "00".equals(params.get("vnp_ResponseCode"))
                && ("00".equals(result.code()) || "02".equals(result.code()));
        String title = success ? "Thanh toán thành công" : "Thanh toán chưa hoàn tất";
        String message = success
                ? "Giao dịch đã được xác nhận. Bạn có thể quay lại ứng dụng để xem vé điện tử."
                : "Giao dịch không thành công hoặc chưa được xác nhận. Vui lòng quay lại ứng dụng và thử lại.";
        String statusClass = success ? "success" : "failed";
        String statusIcon = success ? "✓" : "!";
        String amount = formatReturnedAmount(params.get("vnp_Amount"));
        String transactionNo = safeParam(params, "vnp_TransactionNo", "Đang cập nhật");
        String bankCode = safeParam(params, "vnp_BankCode", "VNPay");
        String txnRef = safeParam(params, "vnp_TxnRef", "Không có");
        String responseCode = safeParam(params, "vnp_ResponseCode", "--");

        return """
                <!doctype html>
                <html lang="vi">
                <head>
                    <meta charset="utf-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <title>%s</title>
                    <style>
                        * { box-sizing: border-box; }
                        body {
                            margin: 0;
                            min-height: 100vh;
                            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif;
                            color: #17202a;
                            background: radial-gradient(circle at top left, rgba(255,255,255,.35), transparent 28rem),
                                        linear-gradient(160deg, #1e88e5 0%%, #0f6fc8 48%%, #0b4fa9 100%%);
                        }
                        .page {
                            min-height: 100vh;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            padding: 28px 18px;
                        }
                        .receipt {
                            width: min(100%%, 420px);
                            overflow: hidden;
                            border-radius: 28px;
                            background: #ffffff;
                            box-shadow: 0 24px 60px rgba(8, 47, 96, .28);
                        }
                        .hero {
                            padding: 34px 28px 26px;
                            text-align: center;
                            background: linear-gradient(180deg, #ffffff 0%%, #f7fbff 100%%);
                        }
                        .badge {
                            width: 78px;
                            height: 78px;
                            display: grid;
                            place-items: center;
                            margin: 0 auto 18px;
                            border-radius: 50%%;
                            font-size: 42px;
                            font-weight: 900;
                        }
                        .success .badge {
                            color: #ffffff;
                            background: linear-gradient(135deg, #18b26b, #0e9f5b);
                            box-shadow: 0 14px 28px rgba(14, 159, 91, .28);
                        }
                        .failed .badge {
                            color: #ffffff;
                            background: linear-gradient(135deg, #ef4444, #dc2626);
                            box-shadow: 0 14px 28px rgba(220, 38, 38, .24);
                        }
                        h1 {
                            margin: 0;
                            font-size: 25px;
                            line-height: 1.25;
                            letter-spacing: 0;
                        }
                        .message {
                            margin: 12px auto 0;
                            max-width: 320px;
                            color: #667085;
                            font-size: 15px;
                            line-height: 1.6;
                        }
                        .details {
                            padding: 22px 24px 8px;
                            border-top: 1px solid #edf1f5;
                        }
                        .row {
                            display: flex;
                            align-items: flex-start;
                            justify-content: space-between;
                            gap: 16px;
                            padding: 13px 0;
                            border-bottom: 1px solid #f0f3f7;
                        }
                        .row:last-child { border-bottom: 0; }
                        .label {
                            color: #7b8794;
                            font-size: 13px;
                            line-height: 1.35;
                        }
                        .value {
                            max-width: 58%%;
                            color: #17202a;
                            font-size: 14px;
                            font-weight: 800;
                            line-height: 1.35;
                            text-align: right;
                            overflow-wrap: anywhere;
                        }
                        .amount {
                            color: #1e88e5;
                            font-size: 16px;
                        }
                        .footer {
                            padding: 22px 24px 26px;
                            text-align: center;
                            background: #fbfdff;
                        }
                        .hint {
                            margin: 0;
                            color: #667085;
                            font-size: 13px;
                            line-height: 1.55;
                        }
                        .brand {
                            margin-top: 16px;
                            color: #1e88e5;
                            font-size: 12px;
                            font-weight: 900;
                            letter-spacing: .08em;
                        }
                    </style>
                </head>
                <body>
                    <main class="page">
                        <section class="receipt %s">
                            <div class="hero">
                                <div class="badge">%s</div>
                                <h1>%s</h1>
                                <p class="message">%s</p>
                            </div>
                            <div class="details">
                                <div class="row">
                                    <div class="label">Số tiền</div>
                                    <div class="value amount">%s</div>
                                </div>
                                <div class="row">
                                    <div class="label">Mã giao dịch</div>
                                    <div class="value">%s</div>
                                </div>
                                <div class="row">
                                    <div class="label">Mã đơn hàng</div>
                                    <div class="value">%s</div>
                                </div>
                                <div class="row">
                                    <div class="label">Ngân hàng</div>
                                    <div class="value">%s</div>
                                </div>
                                <div class="row">
                                    <div class="label">Mã phản hồi</div>
                                    <div class="value">%s</div>
                                </div>
                            </div>
                            <div class="footer">
                                <p class="hint">Giữ trang này làm xác nhận giao dịch, sau đó quay lại ứng dụng Bus Go Tickets để xem vé.</p>
                                <div class="brand">BUS GO TICKETS</div>
                            </div>
                        </section>
                    </main>
                </body>
                </html>
                """.formatted(
                title,
                statusClass,
                statusIcon,
                title,
                message,
                amount,
                transactionNo,
                txnRef,
                bankCode,
                responseCode
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentStatusResponseDTO getPaymentStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        Payment payment = paymentRepository.findByBookingId(bookingId).orElse(null);
        String paymentStatus = payment == null ? null : payment.getStatus().name();
        return PaymentStatusResponseDTO.builder()
                .bookingId(booking.getId())
                .bookingStatus(booking.getStatus().name())
                .paymentStatus(paymentStatus)
                .paid(booking.getStatus() == Booking.BookingStatus.CONFIRMED
                        && payment != null
                        && payment.getStatus() == Payment.PaymentStatus.SUCCESS)
                .build();
    }

    private CallbackResult processCallback(Map<String, String> params) {
        try {
            if (params == null || params.isEmpty()) {
                return new CallbackResult("99", "Input data required");
            }

            String secureHash = params.get("vnp_SecureHash");
            if (!StringUtils.hasText(secureHash) || !secureHash.equalsIgnoreCase(signResponse(params))) {
                return new CallbackResult("97", "Invalid signature");
            }

            String txnRef = params.get("vnp_TxnRef");
            Payment payment = paymentRepository.findByGatewayTxnRef(txnRef).orElse(null);
            if (payment == null) {
                return new CallbackResult("01", "Order not found");
            }

            Booking booking = payment.getBooking();
            String returnedAmount = params.get("vnp_Amount");
            if (!toVnpayAmount(payment.getAmount()).equals(returnedAmount)) {
                return new CallbackResult("04", "Invalid amount");
            }

            if (payment.getStatus() == Payment.PaymentStatus.SUCCESS) {
                return new CallbackResult("02", "Order already confirmed");
            }

            String responseCode = params.get("vnp_ResponseCode");
            String transactionStatus = params.get("vnp_TransactionStatus");
            payment.setResponseCode(responseCode);
            payment.setTransactionStatus(transactionStatus);
            payment.setGatewayTransactionNo(params.get("vnp_TransactionNo"));
            payment.setBankCode(params.get("vnp_BankCode"));
            payment.setCardType(params.get("vnp_CardType"));

            if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
                payment.setStatus(Payment.PaymentStatus.SUCCESS);
                payment.setPaymentTime(LocalDateTime.now(VN_ZONE));
                booking.setStatus(Booking.BookingStatus.CONFIRMED);
            } else {
                payment.setStatus(Payment.PaymentStatus.FAILED);
                booking.setStatus(Booking.BookingStatus.CANCELLED);
            }

            bookingRepository.save(booking);
            payment = paymentRepository.save(payment);
            if (payment.getStatus() == Payment.PaymentStatus.SUCCESS) {
                emailNotificationProducer.publishPaymentSuccessEmail(booking, payment);
            }
            return new CallbackResult("00", "Confirm Success");
        } catch (Exception e) {
            return new CallbackResult("99", "Unknown error");
        }
    }

    private String signResponse(Map<String, String> params) {
        Map<String, String> filtered = new TreeMap<>();
        params.forEach((key, value) -> {
            if (key != null
                    && key.startsWith("vnp_")
                    && !"vnp_SecureHash".equals(key)
                    && !"vnp_SecureHashType".equals(key)
                    && value != null
                    && !value.isBlank()) {
                filtered.put(key, value);
            }
        });
        return hmacSha512(properties.getEffectiveHashSecret(), buildQuery(filtered));
    }

    private String buildQuery(Map<String, String> params) {
        StringBuilder query = new StringBuilder();
        params.forEach((key, value) -> {
            if (value == null || value.isBlank()) {
                return;
            }
            if (!query.isEmpty()) {
                query.append('&');
            }
            query.append(urlEncode(key)).append('=').append(urlEncode(value));
        });
        return query.toString();
    }

    private String hmacSha512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            hmac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot sign VNPay request", e);
        }
    }

    private String toVnpayAmount(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100)).toBigInteger().toString();
    }

    private String formatReturnedAmount(String amount) {
        try {
            if (!StringUtils.hasText(amount)) {
                return "Đang cập nhật";
            }
            BigDecimal value = new BigDecimal(amount).divide(BigDecimal.valueOf(100));
            NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
            return formatter.format(value);
        } catch (Exception e) {
            return "Đang cập nhật";
        }
    }

    private String safeParam(Map<String, String> params, String key, String fallback) {
        String value = params.get(key);
        return StringUtils.hasText(value) ? escapeHtml(value.trim()) : fallback;
    }

    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String normalizeLocale(String locale) {
        if (!StringUtils.hasText(locale)) {
            return "vn";
        }
        String normalized = locale.trim().toLowerCase(Locale.ROOT);
        return "en".equals(normalized) ? "en" : "vn";
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private void validateConfig() {
        if (!StringUtils.hasText(properties.getPaymentUrl())
                || !StringUtils.hasText(properties.getReturnUrl())
                || !StringUtils.hasText(properties.getTmnCode())
                || !StringUtils.hasText(properties.getEffectiveHashSecret())) {
            throw new BusinessException("Thiếu cấu hình VNPay. Điền VNPAY_TMN_CODE và VNPAY_HASH_SECRET hoặc VNPAY_HASH_SECRET_KEY trong file .env, sau đó khởi động lại backend.");
        }
    }

    private record CallbackResult(String code, String message) {
    }
}
