# Stage 1: Build the Spring Boot application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven configuration and source code
COPY pom.xml ./
COPY src ./src

# Package without running tests
RUN mvn clean package -DskipTests

# Optional: List the generated JAR for sanity check
RUN ls -lh target/

# Stage 2: Create a lean runtime image
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the Spring Boot executable JAR explicitly
ARG JAR_FILE=target/*.jar
COPY --from=build /app/${JAR_FILE} app.jar

# Expose the application port
EXPOSE 8080

# Launch the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]