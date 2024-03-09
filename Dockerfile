FROM gradle:8.5.0-jdk21-alpine AS build
RUN mkdir -p /app/sources
RUN mkdir -p /app/built
WORKDIR /app/sources
COPY / /app/sources
RUN gradle shadowJar

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S instance && adduser -S maea -G instance
USER maea

ENV nc_amqp_url                            # amqp://maeasoftworks:normativecontrol@localhost:5672
ENV nc_s3_access_key_id                    # pdh5niPCbqYxGW5BKxGv
ENV nc_s3_bucket 'normative-control'
ENV nc_s3_endpoint                         # http://localhost:9000
ENV nc_s3_region 'US-EAST-1'
ENV nc_s3_secret_key_id                    # xqtc6gXpKy0OIZVVSeinxDd7dd0pbDsOEtU7huJX
ENV nc_amqp_queue_name 'to_be_verified'

COPY --from=build /app/sources/launcher/build/libs/*.jar /app/built/app.jar
ENTRYPOINT java -jar app/built/app.jar