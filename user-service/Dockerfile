# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /build

RUN apk add --no-cache dos2unix

COPY user-service/gradle gradle/
COPY user-service/gradlew user-service/gradlew.bat ./
RUN dos2unix gradlew && chmod +x gradlew

COPY user-service/build.gradle user-service/settings.gradle ./
COPY user-service/src src/

RUN ./gradlew build --no-daemon -x test

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/build/libs/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
