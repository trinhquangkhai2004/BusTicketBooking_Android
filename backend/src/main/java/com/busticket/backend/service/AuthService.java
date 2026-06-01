package com.busticket.backend.service;

import com.busticket.backend.dto.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO authenticateWithFirebase(String idToken, String displayName);
}
