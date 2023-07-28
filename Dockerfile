FROM eclipse-temurin:20-jdk-alpine as cache
RUN mkdir -p /home/gradle/cache_home
RUN mkdir -p /app/build
ENV GRADLE_USER_HOME /home/gradle/cache
COPY build.gradle.kts /app/build
WORKDIR /app/build
RUN gradlew clean build

FROM eclipse-temurin:20-jdk-alpine AS build
COPY --from=cache /home/gradle/cache /home/gradle/.gradle
WORKDIR /app/build
COPY / ./
RUN gradlew bootJar

FROM eclipse-temurin:20-jdk-alpine
RUN addgroup -S instance && adduser -S maea -G instance
USER maea

COPY --from=build /app/build/build/libs/* /app/jar/app.jar
ENTRYPOINT java --enable-preview -jar app/jar/app.jar