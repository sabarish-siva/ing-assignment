FROM maven:3.9.5-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY ../../ /app
WORKDIR /app/orderprocessor
RUN mvn clean package -DskipTests

# Build the application
FROM eclipse-temurin:latest
WORKDIR /app/orderprocessor
COPY --from=builder /app/orderprocessor/target/*.jar /app/ordermanager.jar
ENTRYPOINT ["java", "-jar", "ordermanager.jar"]