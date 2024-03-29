FROM ubuntu:latest
LABEL authors="USER"

ENTRYPOINT ["top", "-b"]
# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk-jammy

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
