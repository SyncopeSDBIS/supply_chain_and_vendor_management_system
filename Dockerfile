# Multi-stage Dockerfile for Supply Chain & Vendor Management System (P2P)
# Java 21 / Spring Boot 3.3.5

# Stage 1: Build Application
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /workspace

# Cache Maven wrapper and dependencies
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw dependency:go-offline -B || true

# Copy source and build
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime Environment
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Non-root user for enterprise container security
RUN groupadd -r supplychain && useradd -r -g supplychain -s /bin/false supplychain

# Install curl for healthcheck
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

COPY --from=builder /workspace/target/supplychain-p2p-*.jar app.jar
RUN chown -R supplychain:supplychain /app

USER supplychain

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-XX:+UseG1GC", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
