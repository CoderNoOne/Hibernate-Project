FROM openjdk:12-jdk-alpine

LABEL maintainer=CoderNoOne

ARG MYSQL_USER
ARG MYSQL_PASSWORD
ARG MYSQL_DATABASE
ARG MYSQL_SERVICE

COPY pom.xml mvnw ./
COPY .mvn .mvn
ADD src src


RUN /mvnw clean package \
-DMYSQL_USER=${MYSQL_USER} \
-DMYSQL_PASSWORD=${MYSQL_PASSWORD} \
-DMYSQL_DATABASE=${MYSQL_DATABASE} \
-DMYSQL_SERVICE=${MYSQL_SERVICE} \
-DskipTests

ENTRYPOINT ["sh", "-c", "java --enable-preview -jar /target/app-uber.jar"]
