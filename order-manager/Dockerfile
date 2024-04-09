FROM maven:3.8.4-openjdk-21-slim AS builder
WORKDIR /app
COPY . .
RUN mvn clean package

FROM adoptopenjdk/openjdk21:alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
