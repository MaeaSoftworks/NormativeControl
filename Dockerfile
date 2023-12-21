FROM gradle:8.4.0-jdk20-alpine as cache
RUN mkdir -p /home/gradle/cache
RUN mkdir -p /app/sources
RUN mkdir -p /app/built
ENV GRADLE_USER_HOME /home/gradle/cache
COPY build.gradle.kts /app/sources
WORKDIR /app/sources
RUN gradle build

FROM gradle:8.4.0-jdk20-alpine AS build
COPY --from=cache /home/gradle/cache /home/gradle/.gradle
WORKDIR /app/sources
COPY / /app/sources
RUN gradle buildFatJar

FROM eclipse-temurin:20-jre-alpine
RUN addgroup -S instance && adduser -S maea -G instance
USER maea

COPY --from=build /app/sources/api/build/libs/api-all.jar /app/built/app.jar
ENTRYPOINT java -jar app/built/app.jar