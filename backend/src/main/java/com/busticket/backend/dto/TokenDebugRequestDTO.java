package com.busticket.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenDebugRequestDTO {
    @NotBlank(message = "Firebase ID token is required")
    private String idToken;
}
