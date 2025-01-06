# 빌드 스테이지
FROM gradle:7.6-jdk17 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 캐시 활용을 위해 Gradle 파일 복사
COPY build.gradle.kts settings.gradle.kts gradle.properties /app/

# 의존성 캐시 미리 로드
RUN gradle dependencies --no-daemon || true

# 애플리케이션 소스 코드 복사
COPY src /app/src

# JAR 파일 빌드
RUN gradle bootJar --no-daemon

# 실행 스테이지
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션 실행 명령어 설정
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]

# 포트 노출
EXPOSE 8080
