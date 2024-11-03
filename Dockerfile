# Start with a base image that includes Java
FROM openjdk:17-jdk-slim AS build

# Set the working directory
WORKDIR /app

# Copy Gradle files and wrapper scripts
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle /app/

# Copy the source code
COPY src /app/src

# Give execute permission to the gradlew script
RUN chmod +x gradlew

# Build the application
RUN ./gradlew build

# Final stage to run the application
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/pabrik-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
