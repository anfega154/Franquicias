FROM eclipse-temurin:21-jdk-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY applications/app-service/build/libs/Franquicias.jar /app/Franquicias.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

USER appuser

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -jar Franquicias.jar" ]
