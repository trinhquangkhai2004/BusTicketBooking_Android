package com.busticket.backend.service;

import com.busticket.backend.dto.TokenDebugResponseDTO;

public interface TokenDebugService {
    TokenDebugResponseDTO inspect(String idToken);
}
