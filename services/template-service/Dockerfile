# === Base Maven build layer: installs dependencies only once ===
FROM maven:3.9.6-eclipse-temurin-21 AS maven-base
WORKDIR /app

COPY pom.xml .
# Pre-fetch all Maven dependencies
RUN mvn -B dependency:go-offline

# === Actual service build ===
FROM maven-base AS build
COPY . template-service/
WORKDIR /app/template-service
RUN mvn -B clean package -DskipTests

# === Final runtime image ===
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/template-service/target/*.jar app.jar

ARG SPRING_PROFILES_ACTIVE=k8s
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
RUN echo "Building template service with profile: $SPRING_PROFILES_ACTIVE"

ENTRYPOINT ["java", "-jar", "app.jar"]