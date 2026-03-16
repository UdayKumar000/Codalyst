# Use an official Java runtime as a parent image
FROM eclipse-temurin:17-jdk

# Set working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/app.jar .

# Set Render's dynamic PORT environment variable (optional default)
ENV PORT 8080

# Expose port (just for documentation)
EXPOSE 8080

# Run the Spring Boot app using Render's PORT
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=$PORT"]