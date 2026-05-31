package com.busticket.backend.service.impl;

import com.busticket.backend.dto.EmailMessageDTO;
import com.busticket.backend.service.EmailDeliveryService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailDeliveryServiceImpl implements EmailDeliveryService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    @Value("${app.mail.audit-bcc:}")
    private String auditBccAddress;

    @Override
    public void send(EmailMessageDTO message) {
        if (message == null || isBlank(message.getTo())) {
            log.warn("Skipping email without recipient. template={}", message == null ? null : message.getTemplate());
            return;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );
            if (!isBlank(fromAddress)) {
                helper.setFrom(fromAddress, "Bus Go Tickets");
            }
            helper.setTo(message.getTo());
            if (shouldAddAuditBcc(message.getTo())) {
                helper.setBcc(auditBccAddress);
            }
            helper.setSubject(message.getSubject());
            helper.setText(renderHtml(message), true);
            mailSender.send(mimeMessage);
            log.info("Email sent. to={}, template={}", message.getTo(), message.getTemplate());
        } catch (Exception ex) {
            log.error("Could not send email. to={}, template={}", message.getTo(), message.getTemplate(), ex);
            throw new IllegalStateException("Could not send email", ex);
        }
    }

    private String renderHtml(EmailMessageDTO message) {
        Map<String, String> vars = message.getVariables() == null ? Map.of() : message.getVariables();
        return switch (message.getTemplate()) {
            case "WELCOME" -> renderWelcome(vars);
            case "BOOKING_CREATED" -> renderBookingCreated(vars);
            case "PAYMENT_SUCCESS" -> renderPaymentSuccess(vars);
            default -> renderGeneric(message);
        };
    }

    private String renderWelcome(Map<String, String> vars) {
        return layout(
                "Chào mừng đến với Bus Go Tickets",
                "Tài khoản của bạn đã sẵn sàng",
                """
                        <p>Xin chào <strong>%s</strong>,</p>
                        <p>Cảm ơn bạn đã đăng ký Bus Go Tickets. Từ bây giờ bạn có thể tìm chuyến xe, chọn ghế và thanh toán vé trực tuyến ngay trong ứng dụng.</p>
                        <div class="note">Nếu cần hỗ trợ, hãy liên hệ %s.</div>
                        """.formatted(
                        escape(vars.get("customerName")),
                        escape(vars.get("supportEmail"))
                )
        );
    }

    private String renderBookingCreated(Map<String, String> vars) {
        return layout(
                "Đã giữ chỗ thành công",
                "Vé của bạn đang chờ thanh toán",
                """
                        <p>Xin chào <strong>%s</strong>,</p>
                        <p>Bus Go Tickets đã giữ chỗ cho booking <strong>#%s</strong>. Vui lòng hoàn tất thanh toán để xác nhận vé.</p>
                        %s
                        <div class="note">Ghế sẽ chỉ được xác nhận sau khi thanh toán thành công.</div>
                        """.formatted(
                        escape(vars.get("customerName")),
                        escape(vars.get("bookingId")),
                        bookingTable(vars)
                )
        );
    }

    private String renderPaymentSuccess(Map<String, String> vars) {
        return layout(
                "Thanh toán thành công",
                "Vé điện tử của bạn đã được xác nhận",
                """
                        <p>Xin chào <strong>%s</strong>,</p>
                        <p>Thanh toán cho booking <strong>#%s</strong> đã được xác nhận. Vui lòng xuất trình vé điện tử khi lên xe.</p>
                        %s
                        <div class="note">Mã giao dịch: %s</div>
                        """.formatted(
                        escape(vars.get("customerName")),
                        escape(vars.get("bookingId")),
                        bookingTable(vars),
                        escape(vars.get("paymentId"))
                )
        );
    }

    private String renderGeneric(EmailMessageDTO message) {
        return layout(
                escape(message.getSubject()),
                "Thông báo từ Bus Go Tickets",
                "<p>Bạn có thông báo mới từ Bus Go Tickets.</p>"
        );
    }

    private String bookingTable(Map<String, String> vars) {
        return """
                <table>
                    <tr><td>Tuyến</td><td>%s</td></tr>
                    <tr><td>Khởi hành</td><td>%s</td></tr>
                    <tr><td>Dự kiến đến</td><td>%s</td></tr>
                    <tr><td>Thời lượng</td><td>%s</td></tr>
                    <tr><td>Biển số xe</td><td>%s</td></tr>
                    <tr><td>Ghế</td><td>%s</td></tr>
                    <tr><td>Tổng tiền</td><td class="amount">%s</td></tr>
                </table>
                """.formatted(
                escape(vars.get("route")),
                escape(vars.get("departureTime")),
                escape(vars.get("arrivalTime")),
                escape(vars.get("duration")),
                escape(vars.get("busPlate")),
                escape(vars.get("seatNumbers")),
                escape(vars.get("totalAmount"))
        );
    }

    private String layout(String title, String subtitle, String content) {
        return """
                <!doctype html>
                <html lang="vi">
                <head>
                    <meta charset="utf-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <style>
                        body { margin: 0; background: #eef4fb; font-family: Arial, sans-serif; color: #17202a; }
                        .wrap { max-width: 620px; margin: 0 auto; padding: 28px 14px; }
                        .card { overflow: hidden; border-radius: 22px; background: #ffffff; box-shadow: 0 18px 48px rgba(16, 65, 116, .16); }
                        .hero { padding: 30px 28px; color: #ffffff; background: linear-gradient(135deg, #1e88e5, #0f6fc8); }
                        .brand { margin-bottom: 18px; font-size: 13px; font-weight: 800; letter-spacing: .08em; }
                        h1 { margin: 0; font-size: 26px; line-height: 1.25; }
                        .subtitle { margin: 8px 0 0; opacity: .9; line-height: 1.55; }
                        .body { padding: 28px; font-size: 15px; line-height: 1.65; }
                        table { width: 100%%; margin: 22px 0; border-collapse: collapse; border: 1px solid #edf1f5; border-radius: 14px; overflow: hidden; }
                        td { padding: 13px 14px; border-bottom: 1px solid #edf1f5; vertical-align: top; }
                        tr:last-child td { border-bottom: 0; }
                        td:first-child { width: 38%%; color: #667085; }
                        td:last-child { text-align: right; font-weight: 700; }
                        .amount { color: #1e88e5; }
                        .note { margin-top: 20px; padding: 14px 16px; border-radius: 14px; background: #f3f8ff; color: #475467; }
                        .closing { margin: 0 28px 28px; padding: 18px; border: 1px solid #e4eefb; border-radius: 16px; background: #f8fbff; color: #344054; }
                        .closing strong { display: block; margin-bottom: 6px; color: #1e88e5; font-size: 15px; }
                        .closing p { margin: 0; font-size: 14px; line-height: 1.55; }
                        .footer { padding: 18px 28px 26px; color: #98a2b3; font-size: 12px; text-align: center; background: #fbfdff; }
                    </style>
                </head>
                <body>
                    <div class="wrap">
                        <section class="card">
                            <div class="hero">
                                <div class="brand">BUS GO TICKETS</div>
                                <h1>%s</h1>
                                <p class="subtitle">%s</p>
                            </div>
                            <div class="body">%s</div>
                            <div class="closing">
                                <strong>Cảm ơn bạn đã tin tưởng Bus Go Tickets</strong>
                                <p>Chúng tôi rất vui được đồng hành cùng chuyến đi của bạn. Chúc bạn có một hành trình an toàn, đúng giờ và thoải mái.</p>
                            </div>
                            <div class="footer">Email này được gửi tự động từ hệ thống Bus Go Tickets.</div>
                        </section>
                    </div>
                </body>
                </html>
                """.formatted(title, subtitle, content);
    }

    private String escape(String value) {
        if (value == null || value.isBlank()) {
            return "Đang cập nhật";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean shouldAddAuditBcc(String recipient) {
        return !isBlank(auditBccAddress) && !auditBccAddress.equalsIgnoreCase(recipient);
    }
}
