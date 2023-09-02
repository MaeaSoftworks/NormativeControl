FROM gradle:8.0.2-jdk19-alpine as cache
RUN mkdir -p /home/gradle/cache
RUN mkdir -p /app/sources
RUN mkdir -p /app/built
ENV GRADLE_USER_HOME /home/gradle/cache
COPY build.gradle.kts /app/sources
WORKDIR /app/sources
RUN gradle clean build -x test

FROM gradle:8.0.2-jdk19-alpine AS build
COPY --from=cache /home/gradle/cache /home/gradle/.gradle
WORKDIR /app/sources
COPY / /app/sources
RUN gradle bootJar

FROM eclipse-temurin:19-jre-alpine
RUN addgroup -S instance && adduser -S maea -G instance
USER maea

COPY --from=build /app/sources/bootstrapper/build/libs/* /app/built/app.jar
ENTRYPOINT java --enable-preview -jar app/built/app.jar