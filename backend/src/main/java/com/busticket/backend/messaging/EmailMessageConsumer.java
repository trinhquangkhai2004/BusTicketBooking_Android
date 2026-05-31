package com.busticket.backend.messaging;

import com.busticket.backend.config.RabbitMQConfig;
import com.busticket.backend.dto.EmailMessageDTO;
import com.busticket.backend.service.EmailDeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailMessageConsumer {
    private final EmailDeliveryService emailDeliveryService;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void consume(EmailMessageDTO message) {
        log.info("Email message received. to={}, template={}", message.getTo(), message.getTemplate());
        try {
            emailDeliveryService.send(message);
        } catch (Exception ex) {
            log.error(
                    "Email message failed and will not be requeued. to={}, template={}",
                    message.getTo(),
                    message.getTemplate(),
                    ex
            );
        }
    }
}
