# ============================================
# Stage 1: Build
# ============================================
FROM gradle:8.12-jdk21 AS builder

WORKDIR /app

# Copy gradle files trước để tận dụng Docker layer caching
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon || true

# Copy source code và build
COPY src ./src
RUN gradle bootJar --no-daemon -x test

# ============================================
# Stage 2: Runtime
# ============================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Tạo user non-root cho security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy JAR từ builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Tạo thư mục cho logs và config
RUN mkdir -p /app/logs /app/config && \
    chown -R appuser:appgroup /app

USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# JVM tuning cho container
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UseContainerSupport"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
