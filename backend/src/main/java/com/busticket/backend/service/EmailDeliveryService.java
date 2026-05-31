package com.busticket.backend.service;

import com.busticket.backend.dto.EmailMessageDTO;

public interface EmailDeliveryService {
    void send(EmailMessageDTO message);
}
