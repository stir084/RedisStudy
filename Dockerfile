# OpenJDK 17을 기반으로 함
FROM openjdk:17-jdk-slim

# 빌드된 Spring Boot 애플리케이션 JAR 파일을 컨테이너 내부로 복사
COPY build/libs/RedisStudy-0.0.1-SNAPSHOT.jar /app.jar

# 8080 포트를 외부에 노출
EXPOSE 8080

# JAR 파일 실행을 위한 ENTRYPOINT 설정
ENTRYPOINT ["java", "-jar", "/app.jar"]