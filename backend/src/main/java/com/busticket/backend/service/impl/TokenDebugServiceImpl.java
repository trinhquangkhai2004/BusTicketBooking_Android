package com.busticket.backend.service.impl;

import com.busticket.backend.dto.TokenDebugResponseDTO;
import com.busticket.backend.exception.BusinessException;
import com.busticket.backend.service.TokenDebugService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.FirebaseApp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class TokenDebugServiceImpl implements TokenDebugService {

    private final ObjectMapper objectMapper;

    @Override
    public TokenDebugResponseDTO inspect(String idToken) {
        try {
            String normalizedToken = idToken == null ? "" : idToken.trim();
            String[] parts = normalizedToken.split("\\.");
            if (parts.length < 2) {
                throw new BusinessException("Invalid JWT format");
            }

            JsonNode header = objectMapper.readTree(new String(
                    Base64.getUrlDecoder().decode(parts[0]),
                    StandardCharsets.UTF_8
            ));
            byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
            JsonNode payload = objectMapper.readTree(new String(payloadBytes, StandardCharsets.UTF_8));

            String audience = text(payload, "aud");
            String issuer = text(payload, "iss");
            String subject = text(payload, "sub");
            String expectedProjectId = FirebaseApp.getInstance().getOptions().getProjectId();

            return TokenDebugResponseDTO.builder()
                    .tokenLength(normalizedToken.length())
                    .segmentCount(parts.length)
                    .segmentLengths(segmentLengths(parts))
                    .headerAlgorithm(text(header, "alg"))
                    .headerKeyId(text(header, "kid"))
                    .tokenAudience(audience)
                    .tokenIssuer(issuer)
                    .tokenSubject(subject)
                    .expectedProjectId(expectedProjectId)
                    .projectMatches(expectedProjectId != null && expectedProjectId.equals(audience))
                    .build();
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Invalid token payload encoding");
        } catch (Exception ex) {
            if (ex instanceof BusinessException businessException) {
                throw businessException;
            }
            throw new BusinessException("Cannot inspect token payload");
        }
    }

    private String text(JsonNode node, String fieldName) {
        JsonNode value = node.get(fieldName);
        return value == null || value.isNull() ? null : value.asText();
    }

    private String segmentLengths(String[] parts) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                builder.append(".");
            }
            builder.append(parts[i].length());
        }
        return builder.toString();
    }
}
