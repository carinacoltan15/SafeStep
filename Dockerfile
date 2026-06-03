# ═══════════════════════════════════════════════════════════════════════════
#  SafeStep — Multi-stage Dockerfile
#  Java 17 · Spring Boot 3.2 · Maven 3.9
#  Stage 1 builds the fat JAR; Stage 2 is the lean runtime image.
# ═══════════════════════════════════════════════════════════════════════════

# ── Stage 1: Build ──────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /build

# Copy the POM first so Maven can download dependencies as a separate
# cached layer — rebuilds are fast as long as pom.xml doesn't change.
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy sources and build the executable JAR (tests skipped for speed)
COPY src ./src
RUN mvn clean package -DskipTests -q

# ── Stage 2: Runtime ────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy only the compiled artifact — keeps the final image small (~180 MB)
COPY --from=builder /build/target/SafeStepProject-1.0-SNAPSHOT.jar app.jar

# Render injects $PORT at runtime; Spring Boot reads it via server.port=${PORT:8080}
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
