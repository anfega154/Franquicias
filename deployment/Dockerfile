FROM gradle:8.14.3-jdk21-alpine AS builder

WORKDIR /workspace
COPY build.gradle settings.gradle main.gradle gradle.properties gradlew sonar.properties /workspace/
COPY gradle /workspace/gradle
COPY applications /workspace/applications
COPY domain /workspace/domain
COPY infrastructure /workspace/infrastructure
RUN chmod +x gradlew && ./gradlew clean bootJar

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY --from=builder /workspace/applications/app-service/build/libs/*.jar /app/app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
ENV SERVER_PORT=8080

EXPOSE 8080

USER appuser

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]