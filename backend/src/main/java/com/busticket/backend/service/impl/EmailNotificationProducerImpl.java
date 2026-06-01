package com.busticket.backend.service.impl;

import com.busticket.backend.config.RabbitMQConfig;
import com.busticket.backend.dto.EmailMessageDTO;
import com.busticket.backend.entity.Booking;
import com.busticket.backend.entity.Payment;
import com.busticket.backend.entity.Seat;
import com.busticket.backend.entity.Trip;
import com.busticket.backend.entity.User;
import com.busticket.backend.service.EmailNotificationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationProducerImpl implements EmailNotificationProducer {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
    private static final Locale VIETNAM_LOCALE = Locale.forLanguageTag("vi-VN");

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishWelcomeEmail(User user) {
        if (user == null) {
            return;
        }
        publish(EmailMessageDTO.builder()
                .to(user.getEmail())
                .subject("Chào mừng bạn đến với Bus Go Tickets")
                .template("WELCOME")
                .variables(Map.of(
                        "customerName", safe(user.getName()),
                        "supportEmail", "travelk.busticketbooking@gmail.com"
                ))
                .build());
    }

    @Override
    public void publishBookingCreatedEmail(Booking booking) {
        if (booking == null) {
            return;
        }
        Map<String, String> variables = bookingVariables(booking);
        publish(EmailMessageDTO.builder()
                .to(booking.getUser().getEmail())
                .subject("Đã giữ chỗ vé xe #" + booking.getId())
                .template("BOOKING_CREATED")
                .variables(variables)
                .build());
    }

    @Override
    public void publishPaymentSuccessEmail(Booking booking, Payment payment) {
        if (booking == null) {
            return;
        }
        Map<String, String> variables = bookingVariables(booking);
        variables.put("paymentId", payment == null ? "Đang cập nhật" : safe(payment.getGatewayTransactionNo()));
        variables.put("paymentMethod", payment == null ? "VNPay" : safe(payment.getMethod().name()));
        publish(EmailMessageDTO.builder()
                .to(booking.getUser().getEmail())
                .subject("Thanh toán thành công vé xe #" + booking.getId())
                .template("PAYMENT_SUCCESS")
                .variables(variables)
                .build());
    }

    private void publish(EmailMessageDTO message) {
        if (message == null || message.getTo() == null || message.getTo().isBlank()) {
            return;
        }
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EMAIL_EXCHANGE,
                    RabbitMQConfig.EMAIL_ROUTING_KEY,
                    message
            );
        } catch (Exception ex) {
            log.warn("Could not publish email notification to RabbitMQ. to={}, template={}",
                    message.getTo(), message.getTemplate(), ex);
        }
    }

    private Map<String, String> bookingVariables(Booking booking) {
        Trip trip = booking.getTrip();
        Map<String, String> variables = new LinkedHashMap<>();
        variables.put("customerName", safe(booking.getUser().getName()));
        variables.put("bookingId", String.valueOf(booking.getId()));
        variables.put("route", routeName(trip));
        variables.put("departureTime", trip.getDepartureTime().format(DATE_TIME_FORMAT));
        variables.put("arrivalTime", trip.getArrivalTime().format(DATE_TIME_FORMAT));
        variables.put("duration", safe(trip.getRoute().getDuration()));
        variables.put("busPlate", safe(trip.getBus().getLicensePlate()));
        variables.put("seatNumbers", booking.getSeats().stream()
                .sorted(Comparator.comparing(Seat::getSeatNumber))
                .map(Seat::getSeatNumber)
                .collect(Collectors.joining(", ")));
        variables.put("totalAmount", formatCurrency(booking.getTotalAmount()));
        variables.put("status", safe(booking.getStatus().name()));
        return variables;
    }

    private String routeName(Trip trip) {
        return trip.getRoute().getDepartureLocation().getName()
                + " → "
                + trip.getRoute().getArrivalLocation().getName();
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0 ₫";
        }
        return NumberFormat.getCurrencyInstance(VIETNAM_LOCALE).format(amount);
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "Đang cập nhật" : value;
    }
}
