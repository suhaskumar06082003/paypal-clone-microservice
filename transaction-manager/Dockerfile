# Build stage — borrows the Gradle wrapper from user-service (transaction-manager has none)
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /build

RUN apk add --no-cache dos2unix

COPY user-service/gradle gradle/
COPY user-service/gradlew user-service/gradlew.bat ./
RUN dos2unix gradlew && chmod +x gradlew

COPY transaction-manager/build.gradle transaction-manager/settings.gradle ./
COPY transaction-manager/src src/

RUN ./gradlew build --no-daemon -x test

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/build/libs/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
