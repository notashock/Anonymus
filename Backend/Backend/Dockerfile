# Use official OpenJDK 17
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the Maven-built jar
COPY target/*.jar app.jar

# Expose the port your Spring Boot app uses
EXPOSE 8080

# Run Spring Boot
ENTRYPOINT ["java","-jar","/app/app.jar"]
