FROM gradle:8.0.2-jdk19-alpine as cache
RUN mkdir -p /home/gradle/cache
RUN mkdir -p /app/build
ENV GRADLE_USER_HOME /home/gradle/cache
COPY build.gradle.kts /app/build
WORKDIR /app/build
RUN gradle clean build -x test -i --stacktrace

FROM gradle:8.0.2-jdk19-alpine AS build
COPY --from=cache /home/gradle/cache /home/gradle/.gradle
WORKDIR /app/build
COPY / ./
RUN gradle bootJar

FROM eclipse-temurin:19-jre-alpine
RUN addgroup -S instance && adduser -S maea -G instance
USER maea

COPY --from=build /app/build/build/libs/* /app/jar/app.jar
ENTRYPOINT java --enable-preview -jar app/jar/app.jar