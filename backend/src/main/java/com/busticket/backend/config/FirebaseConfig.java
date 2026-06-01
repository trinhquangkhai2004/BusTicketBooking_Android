package com.busticket.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
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
                log.warn("Firebase config file not found or invalid. FCM will not be available.");
                return;
            }

            try (serviceAccount) {
                GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .setProjectId(resolveProjectId(credentials))
                        .build();

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                    log.info("Firebase initialized successfully.");
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            log.error("Failed to initialize Firebase from '{}': {}", firebaseConfigFile, e.getMessage());
        }
    }

    private String resolveProjectId(GoogleCredentials credentials) {
        if (credentials instanceof ServiceAccountCredentials serviceAccountCredentials) {
            return serviceAccountCredentials.getProjectId();
        }

        return null;
    }

    @Bean
    @Lazy
    public FirebaseAuth firebaseAuth() {
        if (FirebaseApp.getApps().isEmpty()) {
            throw new IllegalStateException(
                    "Firebase is not initialized. Check firebase.config-file or FIREBASE_CONFIG_FILE."
            );
        }
        return FirebaseAuth.getInstance();
    }

    @Bean
    @Lazy
    public FirebaseMessaging firebaseMessaging() {
        if (FirebaseApp.getApps().isEmpty()) {
            throw new IllegalStateException(
                    "Firebase is not initialized. Check firebase.config-file or FIREBASE_CONFIG_FILE."
            );
        }
        return FirebaseMessaging.getInstance();
    }

    private InputStream getFirebaseConfigStream() throws IOException {
        Path dockerPath = Path.of("/app/config/" + firebaseConfigFile);
        if (Files.exists(dockerPath)) {
            if (!Files.isRegularFile(dockerPath) || Files.size(dockerPath) == 0) {
                log.warn("Firebase config at {} is not a valid non-empty file.", dockerPath);
                return null;
            }
            log.info("Loading Firebase config from Docker mount: {}", dockerPath);
            return new FileInputStream(dockerPath.toFile());
        }

        ClassPathResource resource = new ClassPathResource(firebaseConfigFile);
        if (resource.exists()) {
            if (resource.contentLength() == 0) {
                log.warn("Firebase config on classpath '{}' is not a valid non-empty file.", firebaseConfigFile);
                return null;
            }
            log.info("Loading Firebase config from classpath: {}", firebaseConfigFile);
            return resource.getInputStream();
        }

        return null;
    }
}
