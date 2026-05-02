package com.busticket.backend.controller;

import com.busticket.backend.dto.AuthRequestDTO;
import com.busticket.backend.dto.AuthResponseDTO;
import com.busticket.backend.dto.TokenDebugRequestDTO;
import com.busticket.backend.dto.TokenDebugResponseDTO;
import com.busticket.backend.service.AuthService;
import com.busticket.backend.service.TokenDebugService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenDebugService tokenDebugService;

    @PostMapping("/firebase")
    public ResponseEntity<AuthResponseDTO> authenticateWithFirebase(@Valid @RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authService.authenticateWithFirebase(request.getIdToken(), request.getDisplayName()));
    }

    @PostMapping("/debug-token")
    public ResponseEntity<TokenDebugResponseDTO> debugToken(@Valid @RequestBody TokenDebugRequestDTO request) {
        return ResponseEntity.ok(tokenDebugService.inspect(request.getIdToken()));
    }
}
