package com.busticket.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "vnpay")
public class VnpayProperties {
    private String paymentUrl;
    private String returnUrl;
    private String tmnCode;
    private String hashSecret;
    private String hashSecretKey;
    private int expireMinutes = 10;

    public String getEffectiveHashSecret() {
        return hasText(hashSecret) ? hashSecret : hashSecretKey;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
