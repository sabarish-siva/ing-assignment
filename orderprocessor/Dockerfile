FROM maven:3.9.5-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:latest
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/orderprocessor.jar
ENTRYPOINT ["java","-jar","orderprocessor.jar"]