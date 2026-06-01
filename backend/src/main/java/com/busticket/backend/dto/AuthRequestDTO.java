package com.busticket.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequestDTO {
    @NotBlank(message = "Firebase ID token is required")
    private String idToken;

    private String displayName;
}
