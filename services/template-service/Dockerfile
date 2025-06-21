# === Base Maven build layer: installs dependencies only once ===
FROM maven:3.9.6-eclipse-temurin-21 AS maven-base
WORKDIR /app

COPY pom.xml .
COPY api-gateway-service/pom.xml api-gateway-service/
COPY user-service/pom.xml user-service/
COPY template-service/pom.xml template-service/
COPY form-service/pom.xml form-service/
# Pre-fetch all Maven dependencies
RUN mvn -B dependency:go-offline

# === Actual service build ===
FROM maven-base AS build
COPY template-service/ template-service/
COPY pom.xml .
RUN mvn -B clean package -pl template-service -am -DskipTests

# === Final runtime image ===
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/template-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]