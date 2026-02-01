# Multi-stage build: build Spring Boot jar, then run on JRE

# Stage 1: build all modules and produce Spring Boot jar
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build

# Copy full repo (root pom + modules)
COPY . .

# Build via root aggregator to compile framework + project
RUN mvn -q -DskipTests -f ./pom.xml clean package

# Stage 2: run Spring Boot jar on a slim JRE
FROM eclipse-temurin:17-jre
WORKDIR /app
# Copy the built jar from the project module (version-agnostic)
COPY --from=build /build/project/target/*.jar /app/app.jar

# Expose the same port as configured (defaults to 8088; overridable via env)
EXPOSE 8088

# Run the Spring Boot application
ENTRYPOINT ["java","-jar","/app/app.jar"]
