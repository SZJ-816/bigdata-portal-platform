FROM maven:3.8-openjdk-11 as build

WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline -B -DskipTests || true

COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:11-jre-alpine

RUN apk add --no-cache curl && \
    addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY --from=build /app/target/portal-1.0.0.jar app.jar

RUN mkdir -p /app/logs && chown appuser:appgroup /app/logs

USER appuser

EXPOSE 8090

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]
