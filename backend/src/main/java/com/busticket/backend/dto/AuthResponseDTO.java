package com.busticket.backend.dto;

import com.busticket.backend.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDTO {
    private Long userId;
    private String firebaseUid;
    private String name;
    private String email;
    private String phone;
    private User.Role role;
    private boolean newUser;
}
