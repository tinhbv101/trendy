# Multi-stage build for Spring Boot application
# Stage 1: Build
FROM gradle:8.5-jdk17 AS build

WORKDIR /app

# Copy gradle files
COPY build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle

# Download dependencies (cached layer)
RUN gradle dependencies --no-daemon || true

# Copy source code
COPY src ./src

# Build application
RUN gradle clean build -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:17-jre

WORKDIR /app

# Create non-root user
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy built jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check (curl is available in eclipse-temurin base image)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]
