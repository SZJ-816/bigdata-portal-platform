FROM maven:3.8-openjdk-11 as build

WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline -B -DskipTests || true

COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:11-jdk-alpine

RUN apk add --no-cache curl

WORKDIR /app

COPY --from=build /app/target/portal-1.0.0.jar app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]
