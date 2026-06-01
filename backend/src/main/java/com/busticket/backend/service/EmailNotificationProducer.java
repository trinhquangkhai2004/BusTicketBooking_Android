package com.busticket.backend.service;

import com.busticket.backend.entity.Booking;
import com.busticket.backend.entity.Payment;
import com.busticket.backend.entity.User;

public interface EmailNotificationProducer {
    void publishWelcomeEmail(User user);

    void publishBookingCreatedEmail(Booking booking);

    void publishPaymentSuccessEmail(Booking booking, Payment payment);
}
