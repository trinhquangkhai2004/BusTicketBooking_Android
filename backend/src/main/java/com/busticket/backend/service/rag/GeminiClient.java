package com.busticket.backend.service.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GeminiClient {

    private final RestClient restClient;
    private final String apiKey;
    private final String generationModel;
    private final String embeddingModel;

    public GeminiClient(@Value("${app.gemini.api-key:}") String apiKey,
                        @Value("${app.gemini.generation-model:gemini-2.5-flash}") String generationModel,
                        @Value("${app.gemini.embedding-model:gemini-embedding-001}") String embeddingModel) {
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .build();
        this.apiKey = apiKey;
        this.generationModel = generationModel;
        this.embeddingModel = embeddingModel;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    public List<Double> embed(String text) {
        ensureConfigured();

        Map<String, Object> body = Map.of(
                "model", "models/" + embeddingModel,
                "content", content(text)
        );

        Map<?, ?> response = restClient.post()
                .uri("/models/{model}:embedContent?key={key}", embeddingModel, apiKey)
                .body(body)
                .retrieve()
                .body(Map.class);

        Object embedding = response == null ? null : response.get("embedding");
        if (!(embedding instanceof Map<?, ?> embeddingMap)) {
            throw new IllegalStateException("Gemini embedding response is missing embedding");
        }

        Object values = embeddingMap.get("values");
        if (!(values instanceof List<?> rawValues)) {
            throw new IllegalStateException("Gemini embedding response is missing values");
        }

        List<Double> result = new ArrayList<>(rawValues.size());
        for (Object value : rawValues) {
            if (value instanceof Number number) {
                result.add(number.doubleValue());
            }
        }
        return result;
    }

    public String generate(String prompt) {
        ensureConfigured();

        Map<String, Object> body = Map.of(
                "contents", List.of(content(prompt)),
                "generationConfig", Map.of(
                        "temperature", 0.2,
                        "topP", 0.8,
                        "maxOutputTokens", 512
                )
        );

        Map<?, ?> response = restClient.post()
                .uri("/models/{model}:generateContent?key={key}", generationModel, apiKey)
                .body(body)
                .retrieve()
                .body(Map.class);

        return extractText(response);
    }

    private Map<String, Object> content(String text) {
        return Map.of("parts", List.of(Map.of("text", text)));
    }

    private String extractText(Map<?, ?> response) {
        Object candidates = response == null ? null : response.get("candidates");
        if (!(candidates instanceof List<?> candidateList) || candidateList.isEmpty()) {
            throw new IllegalStateException("Gemini response has no candidates");
        }

        Object firstCandidate = candidateList.getFirst();
        if (!(firstCandidate instanceof Map<?, ?> candidateMap)) {
            throw new IllegalStateException("Gemini response candidate is invalid");
        }

        Object content = candidateMap.get("content");
        if (!(content instanceof Map<?, ?> contentMap)) {
            throw new IllegalStateException("Gemini response content is invalid");
        }

        Object parts = contentMap.get("parts");
        if (!(parts instanceof List<?> partList) || partList.isEmpty()) {
            throw new IllegalStateException("Gemini response content has no parts");
        }

        Object firstPart = partList.getFirst();
        if (!(firstPart instanceof Map<?, ?> partMap)) {
            throw new IllegalStateException("Gemini response part is invalid");
        }

        Object text = partMap.get("text");
        if (!(text instanceof String reply) || reply.isBlank()) {
            throw new IllegalStateException("Gemini response text is empty");
        }
        return reply.trim();
    }

    private void ensureConfigured() {
        if (!isConfigured()) {
            throw new IllegalStateException("Gemini API key is not configured");
        }
    }
}
