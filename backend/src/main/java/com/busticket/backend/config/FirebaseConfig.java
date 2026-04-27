package com.busticket.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.config-file:firebase-service-account.json}")
    private String firebaseConfigFile;

    @PostConstruct
    public void initialize() {
        try {
            InputStream serviceAccount = getFirebaseConfigStream();

            if (serviceAccount == null) {
                log.warn("Firebase config file not found. FCM will not be available.");
                return;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase initialized successfully.");
            }
        } catch (IOException e) {
            log.error("Failed to initialize Firebase: {}", e.getMessage());
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }

    private InputStream getFirebaseConfigStream() throws IOException {
        // Thử đọc từ Docker mount path trước
        Path dockerPath = Path.of("/app/config/" + firebaseConfigFile);
        if (Files.exists(dockerPath)) {
            log.info("Loading Firebase config from Docker mount: {}", dockerPath);
            return new FileInputStream(dockerPath.toFile());
        }

        // Fallback: đọc từ classpath (local development)
        ClassPathResource resource = new ClassPathResource(firebaseConfigFile);
        if (resource.exists()) {
            log.info("Loading Firebase config from classpath: {}", firebaseConfigFile);
            return resource.getInputStream();
        }

        return null;
    }
}
