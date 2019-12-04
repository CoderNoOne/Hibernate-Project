FROM openjdk:12-jdk-alpine

LABEL maintainer=CoderNoOne

ARG MYSQL_USER
ARG MYSQL_PASSWORD
ARG MYSQL_DATABASE
ARG MYSQL_SERVICE

COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src/main src/main

RUN /mvnw clean package \
-DMYSQL_USER=${MYSQL_USER} \
-DMYSQL_PASSWORD=${MYSQL_PASSWORD} \
-DMYSQL_DATABASE=${MYSQL_DATABASE} \
-DMYSQL_SERVICE=${MYSQL_SERVICE} \
-DskipTests

RUN rm -r src \
&& rm -r .mvn \
&& unlink mvnw \
&& unlink pom.xml
#RUN cd target find ! -name "perhb-1.0-SNAPSHOT.jar" | xargs rm -r

ENTRYPOINT ["sh", "-c", "java --enable-preview -jar /target/app-uber.jar"]
