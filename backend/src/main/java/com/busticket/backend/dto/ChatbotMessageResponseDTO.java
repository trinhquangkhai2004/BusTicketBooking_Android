package com.busticket.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatbotMessageResponseDTO {
    private String reply;
}
