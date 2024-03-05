FROM gradle:8.5.0-jdk21-alpine as cache
RUN mkdir -p /home/gradle/cache
RUN mkdir -p /app/sources
RUN mkdir -p /app/built
ENV GRADLE_USER_HOME /home/gradle/cache
COPY launcher/build.gradle.kts /app/sources
WORKDIR /app/sources
RUN gradle build

FROM gradle:8.5.0-jdk21-alpine AS build
COPY --from=cache /home/gradle/cache /home/gradle/.gradle
WORKDIR /app/sources
COPY / /app/sources
RUN gradle buildFatJar

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S instance && adduser -S maea -G instance
USER maea

# ENV nc_amqp_url=amqp://maeasoftworks:normativecontrol@localhost:5672
# ENV nc_s3_access_key_id=pdh5niPCbqYxGW5BKxGv
# ENV nc_s3_bucket=normative-control
# ENV nc_s3_endpoint=http://localhost:9000
# ENV nc_s3_region=US-EAST-1
# ENV nc_s3_secret_key_id=xqtc6gXpKy0OIZVVSeinxDd7dd0pbDsOEtU7huJX
# ENV nc_amqp_queue_name=to_be_verified

COPY --from=build /app/sources/api/build/libs/api-all.jar /app/built/app.jar
ENTRYPOINT java -jar app/built/app.jar