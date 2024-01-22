# syntax=docker/dockerfile:1

# 베이스 이미지 설정
FROM eclipse-temurin:17-jdk-jammy

# 라벨 설정
LABEL authors="gyungtaek"

# 빌드된 jar 파일을 Docker 이미지에 복사
COPY build/libs/*.jar app.jar

# 컨테이너가 시작되었을 때 실행될 명령어
ENTRYPOINT ["java","-jar","app.jar"]
