package com.busticket.backend.controller;

import com.busticket.backend.dto.ChatbotMessageRequestDTO;
import com.busticket.backend.dto.ChatbotMessageResponseDTO;
import com.busticket.backend.service.ChatbotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/message")
    public ResponseEntity<ChatbotMessageResponseDTO> sendMessage(
            @Valid @RequestBody ChatbotMessageRequestDTO request
    ) {
        return ResponseEntity.ok(ChatbotMessageResponseDTO.builder()
                .reply(chatbotService.reply(request.getMessage()))
                .build());
    }
}
