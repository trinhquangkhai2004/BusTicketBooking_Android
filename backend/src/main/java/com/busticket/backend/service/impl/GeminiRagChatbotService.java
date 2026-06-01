package com.busticket.backend.service.impl;

import com.busticket.backend.entity.Trip;
import com.busticket.backend.repository.TripRepository;
import com.busticket.backend.service.ChatbotService;
import com.busticket.backend.service.rag.GeminiClient;
import com.busticket.backend.service.rag.KnowledgeChunk;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiRagChatbotService implements ChatbotService {

    private static final String SUPPORT_EMAIL = "travelk.busticketbooking@gmail.com";
    private static final int MAX_TRIPS_IN_CONTEXT = 20;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,###");

    private final GeminiClient geminiClient;
    private final TripRepository tripRepository;

    @Value("${app.gemini.top-k:3}")
    private int topK;

    private volatile List<KnowledgeChunk> knowledgeBase;

    @Override
    public String reply(String message) {
        String question = message == null ? "" : message.trim();
        if (question.isBlank()) {
            return "Bạn vui lòng nhập câu hỏi để mình hỗ trợ.";
        }

        if (!geminiClient.isConfigured()) {
            return "Chatbot AI chưa được cấu hình Gemini API key. Bạn hãy thêm GEMINI_API_KEY vào file .env rồi restart backend.";
        }

        try {
            List<Double> questionEmbedding = geminiClient.embed(question);
            List<KnowledgeChunk> contexts = retrieve(questionEmbedding);
            String prompt = buildPrompt(question, contexts, buildLiveSystemContext(question));
            return geminiClient.generate(prompt);
        } catch (Exception exception) {
            log.warn("Gemini RAG chatbot failed", exception);
            return "Hiện tại chatbot AI chưa xử lý được câu hỏi này. Bạn có thể thử lại sau hoặc liên hệ hỗ trợ qua "
                    + SUPPORT_EMAIL + ".";
        }
    }

    private List<KnowledgeChunk> retrieve(List<Double> questionEmbedding) {
        return getKnowledgeBase().stream()
                .map(chunk -> new ScoredChunk(chunk, cosineSimilarity(questionEmbedding, chunk.embedding())))
                .sorted(Comparator.comparingDouble(ScoredChunk::score).reversed())
                .limit(Math.max(1, topK))
                .map(ScoredChunk::chunk)
                .toList();
    }

    private List<KnowledgeChunk> getKnowledgeBase() {
        List<KnowledgeChunk> current = knowledgeBase;
        if (current != null) {
            return current;
        }

        synchronized (this) {
            if (knowledgeBase == null) {
                knowledgeBase = buildKnowledgeBase();
            }
            return knowledgeBase;
        }
    }

    private List<KnowledgeChunk> buildKnowledgeBase() {
        List<RawChunk> rawChunks = loadFaqChunks();
        List<KnowledgeChunk> chunks = new ArrayList<>();
        for (RawChunk rawChunk : rawChunks) {
            chunks.add(new KnowledgeChunk(
                    rawChunk.title(),
                    rawChunk.content(),
                    geminiClient.embed(rawChunk.title() + "\n" + rawChunk.content())
            ));
        }
        log.info("Loaded {} chatbot RAG chunks", chunks.size());
        return chunks;
    }

    private List<RawChunk> loadFaqChunks() {
        try {
            ClassPathResource resource = new ClassPathResource("rag/bus-go-faq.md");
            String text = resource.getContentAsString(StandardCharsets.UTF_8);
            List<RawChunk> chunks = new ArrayList<>();

            String currentTitle = "Thong tin chung";
            StringBuilder currentContent = new StringBuilder();
            for (String line : text.split("\\R")) {
                if (line.startsWith("## ")) {
                    addChunk(chunks, currentTitle, currentContent);
                    currentTitle = line.substring(3).trim();
                    currentContent = new StringBuilder();
                } else {
                    currentContent.append(line).append('\n');
                }
            }
            addChunk(chunks, currentTitle, currentContent);
            return chunks;
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot load chatbot FAQ knowledge base", exception);
        }
    }

    private void addChunk(List<RawChunk> chunks, String title, StringBuilder content) {
        String cleanedContent = content.toString().trim();
        if (!cleanedContent.isBlank()) {
            chunks.add(new RawChunk(title, cleanedContent));
        }
    }

    private String buildLiveSystemContext(String question) {
        if (!isTripListQuestion(question)) {
            return "";
        }

        List<Trip> trips = tripRepository.findScheduledTripsForChatbot();
        if (trips.isEmpty()) {
            return "Danh sach chuyen di hien tai: chua co chuyen xe nao dang o trang thai SCHEDULED.";
        }

        StringBuilder builder = new StringBuilder("Danh sach chuyen xe dang mo ban trong he thong:\n");
        trips.stream()
                .limit(MAX_TRIPS_IN_CONTEXT)
                .forEach(trip -> builder.append("- Ma chuyen #")
                        .append(trip.getId())
                        .append(": ")
                        .append(trip.getRoute().getDepartureLocation().getName())
                        .append(" -> ")
                        .append(trip.getRoute().getArrivalLocation().getName())
                        .append(", khoi hanh ")
                        .append(trip.getDepartureTime().format(DATE_TIME_FORMATTER))
                        .append(", den du kien ")
                        .append(trip.getArrivalTime().format(DATE_TIME_FORMATTER))
                        .append(", thoi luong ")
                        .append(trip.getRoute().getDuration())
                        .append(", gia ")
                        .append(CURRENCY_FORMAT.format(trip.getPrice()))
                        .append(" VND")
                        .append(", xe ")
                        .append(trip.getBus().getLicensePlate())
                        .append(".\n"));

        if (trips.size() > MAX_TRIPS_IN_CONTEXT) {
            builder.append("He thong con ")
                    .append(trips.size() - MAX_TRIPS_IN_CONTEXT)
                    .append(" chuyen khac. Hay yeu cau khach loc theo diem di, diem den va ngay khoi hanh de xem day du hon.");
        }
        return builder.toString();
    }

    private boolean isTripListQuestion(String question) {
        String normalized = Normalizer.normalize(question, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
        return (normalized.contains("chuyen") || normalized.contains("tuyen") || normalized.contains("lich"))
                && (normalized.contains("danh sach")
                || normalized.contains("tat ca")
                || normalized.contains("co nhung")
                || normalized.contains("thong tin")
                || normalized.contains("ban co")
                || normalized.contains("dang co"));
    }

    private String buildPrompt(String question, List<KnowledgeChunk> contexts, String liveSystemContext) {
        StringBuilder contextBuilder = new StringBuilder();
        for (KnowledgeChunk context : contexts) {
            contextBuilder
                    .append("### ").append(context.title()).append('\n')
                    .append(context.content()).append("\n\n");
        }
        if (!liveSystemContext.isBlank()) {
            contextBuilder
                    .append("### Du lieu he thong hien tai\n")
                    .append(liveSystemContext)
                    .append("\n\n");
        }

        return """
                Bạn là chatbot hỗ trợ khách hàng của Bus Go Tickets.
                Trả lời bằng tiếng Việt, ngắn gọn, lịch sự và thực tế.
                Chỉ dùng thông tin trong CONTEXT. Nếu CONTEXT không đủ, nói rõ là chưa có thông tin và hướng dẫn liên hệ %s.
                Không bịa chính sách, không hứa hoàn tiền tự động, không yêu cầu khách cung cấp mật khẩu.

                CONTEXT:
                %s

                CÂU HỎI KHÁCH HÀNG:
                %s
                """.formatted(SUPPORT_EMAIL, contextBuilder, question);
    }

    private double cosineSimilarity(List<Double> left, List<Double> right) {
        if (left.isEmpty() || right.isEmpty() || left.size() != right.size()) {
            return 0.0;
        }

        double dot = 0.0;
        double leftMagnitude = 0.0;
        double rightMagnitude = 0.0;
        for (int i = 0; i < left.size(); i++) {
            double leftValue = left.get(i);
            double rightValue = right.get(i);
            dot += leftValue * rightValue;
            leftMagnitude += leftValue * leftValue;
            rightMagnitude += rightValue * rightValue;
        }

        if (leftMagnitude == 0.0 || rightMagnitude == 0.0) {
            return 0.0;
        }
        return dot / (Math.sqrt(leftMagnitude) * Math.sqrt(rightMagnitude));
    }

    private record RawChunk(String title, String content) {
    }

    private record ScoredChunk(KnowledgeChunk chunk, double score) {
    }
}
