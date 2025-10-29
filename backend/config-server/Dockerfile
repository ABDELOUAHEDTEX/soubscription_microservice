FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the JAR file
COPY target/config-server-1.0.0.jar /app/config-server-1.0.0.jar

# Set permissions
RUN chmod 755 /app/config-server-1.0.0.jar

# Default environment variables
ENV SPRING_PROFILES_ACTIVE=native,docker
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Expose port
EXPOSE 8888

# Healthcheck
HEALTHCHECK --interval=30s --timeout=10s --retries=5 --start-period=60s \
    CMD curl -f http://localhost:8888/actuator/health || exit 1

# Run the application
CMD ["java", "-jar", "/app/config-server-1.0.0.jar"]