package com.busticket.backend.service.rag;

import java.util.List;

public record KnowledgeChunk(
        String title,
        String content,
        List<Double> embedding
) {
}
