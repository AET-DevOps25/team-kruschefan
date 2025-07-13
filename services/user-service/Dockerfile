# === Base Maven build layer: installs dependencies only once ===
FROM maven:3.9.6-eclipse-temurin-21 AS maven-base
WORKDIR /app

COPY pom.xml .
# Pre-fetch all Maven dependencies
RUN mvn -B dependency:go-offline

# === Actual service build ===
FROM maven-base AS build
COPY . user-service/
WORKDIR /app/user-service
RUN mvn -B clean package -DskipTests

# === Final runtime image ===
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/user-service/target/*.jar app.jar

ARG DEPLOYMENT=k8s
ENV DEPLOYMENT=${DEPLOYMENT}

ENTRYPOINT ["java", "-jar", "app.jar"]