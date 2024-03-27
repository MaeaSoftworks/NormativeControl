FROM gradle:8.5.0-jdk21-alpine AS build
RUN mkdir -p /app/sources
RUN mkdir -p /app/built
WORKDIR /app/sources
COPY / /app/sources
RUN gradle shadowJar

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S instance && adduser -S maea -G instance
USER maea

ENV nc_amqp_url 'nc_amqp_url is not set'
ENV nc_s3_access_key_id 'nc_s3_access_key_id is not set'
ENV nc_s3_secret_key_id 'nc_s3_secret_key_id is not set'
ENV nc_s3_bucket 'normative-control'
ENV nc_s3_endpoint 'nc_s3_endpoint is not set'
ENV nc_s3_region 'US-EAST-1'
ENV nc_amqp_queue_name 'to_be_verified'
ENV nc_db_url 'nc_db_url is not set'
ENV nc_db_user 'nc_db_user is not set'
ENV nc_db_password 'nc_db_password is not set'

COPY --from=build /app/sources/launcher/build/libs/*.jar /app/built/app.jar
ENTRYPOINT java -jar app/built/app.jar