# Use lightweight JDK 17 image
FROM openjdk:17-jdk-slim

# Create app directory and set it as working directory
WORKDIR /app

# Optional: define a volume (useful for logs or temporary files)
VOLUME /tmp

# Copy the packaged Spring Boot application JAR file into the container
COPY Core/target/Core-1.0-SNAPSHOT.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
