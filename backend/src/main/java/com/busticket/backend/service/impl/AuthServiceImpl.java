package com.busticket.backend.service.impl;

import com.busticket.backend.dto.AuthResponseDTO;
import com.busticket.backend.entity.User;
import com.busticket.backend.exception.ServiceUnavailableException;
import com.busticket.backend.exception.UnauthorizedException;
import com.busticket.backend.repository.UserRepository;
import com.busticket.backend.service.AuthService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final FirebaseAuth firebaseAuth;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AuthResponseDTO authenticateWithFirebase(String idToken, String displayName) {
        FirebaseToken decodedToken = verifyToken(idToken);
        String firebaseUid = decodedToken.getUid();
        String email = decodedToken.getEmail();

        if (email == null || email.isBlank()) {
            throw new UnauthorizedException("Firebase token does not contain an email");
        }

        User user = userRepository.findByFirebaseUid(firebaseUid)
                .or(() -> userRepository.findByEmail(email))
                .orElse(null);
        boolean newUser = user == null;

        if (newUser) {
            user = User.builder()
                    .firebaseUid(firebaseUid)
                    .email(email)
                    .name(resolveName(displayName, decodedToken, email))
                    .role(User.Role.CUSTOMER)
                    .build();
        } else {
            user.setFirebaseUid(firebaseUid);
            user.setEmail(email);
            user.setName(resolveName(displayName, decodedToken, email));
        }

        user = userRepository.save(user);

        return AuthResponseDTO.builder()
                .userId(user.getId())
                .firebaseUid(user.getFirebaseUid())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .newUser(newUser)
                .build();
    }

    private FirebaseToken verifyToken(String idToken) {
        String normalizedToken = idToken == null ? "" : idToken.trim();
        try {
            return firebaseAuth.verifyIdToken(normalizedToken);
        } catch (FirebaseAuthException ex) {
            log.warn(
                    "Firebase ID token verification failed. tokenLength={}, segmentCount={}, errorCode={}, message={}",
                    normalizedToken.length(),
                    normalizedToken.isBlank() ? 0 : normalizedToken.split("\\.").length,
                    ex.getErrorCode(),
                    ex.getMessage()
            );
            throw new UnauthorizedException("Invalid Firebase ID token: " + ex.getErrorCode());
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid Firebase ID token input: {}", ex.getMessage());
            throw new UnauthorizedException("Invalid Firebase ID token");
        } catch (IllegalStateException ex) {
            throw new ServiceUnavailableException("Firebase authentication is not available");
        }
    }

    private String resolveName(String displayName, FirebaseToken decodedToken, String email) {
        if (displayName != null && !displayName.isBlank()) {
            return displayName.trim();
        }

        String name = decodedToken.getName();
        if (name != null && !name.isBlank()) {
            return name;
        }

        int atIndex = email.indexOf('@');
        return atIndex > 0 ? email.substring(0, atIndex) : email;
    }
}
