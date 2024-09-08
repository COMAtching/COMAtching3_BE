# 1단계: 빌드 이미지 설정
#FROM openjdk:17-jdk-slim AS builder

# 작업 디렉토리 설정
#WORKDIR /app

# Gradle Wrapper 및 소스 파일 복사
#COPY gradlew .
#COPY gradle ./gradle
#COPY build.gradle .
#COPY settings.gradle .
#COPY src ./src

# 빌드 실행
#RUN chmod +x ./gradlew
#RUN gradlew build -x test

# 2단계: 실행 이미지 설정
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 결과물 복사
COPY build/libs/comatching3-0.0.1-SNAPSHOT.jar comatching3.jar

# 애플리케이션 실행 명령어 설정
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "comatching3.jar"]

# 포트 노출
EXPOSE 8080
