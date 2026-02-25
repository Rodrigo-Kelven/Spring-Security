# ===== STAGE 1: build =====
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
#RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package -DskipTests

# ===== STAGE 2: runtime =====
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN useradd -r -u 1001 spring
USER spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8181
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-XX:+UseG1GC","-XX:+UseStringDeduplication","-XX:+AlwaysPreTouch","-jar", "app.jar"]