# Use a maven image as the builder for the ordermanager
FROM maven:3.9.5-eclipse-temurin-21-alpine AS builder_ordermanager

WORKDIR /app/ordermanager

COPY pom.xml /app
COPY ./ordercommon /app/ordercommon
COPY ./orderprocessor /app/orderprocessor

# Copy the necessary files for building the ordermanager image
COPY ./ordermanager /app/ordermanager

# Set the working directory
WORKDIR /app
RUN mvn clean install -DskipTests

WORKDIR /app/ordermanager
RUN mvn clean package spring-boot:repackage -DskipTests

WORKDIR /app/orderprocessor
RUN mvn clean package spring-boot:repackage -DskipTests
# Use a suitable base image for the final ordermanager image
FROM eclipse-temurin:latest AS ordermanager_image

WORKDIR /app/ordermanager

# Copy the built JAR file from the builder_ordermanager stage to the final ordermanager image
COPY --from=builder_ordermanager /app/ordermanager/target/*.jar /app/ordermanager.jar

# Define the entrypoint for the ordermanager
CMD ["java", "-jar", "/app/ordermanager.jar"]

FROM eclipse-temurin:latest AS orderprocessor_image

WORKDIR /app/orderprocessor

COPY --from=builder_ordermanager /app/orderprocessor/target/*.jar /app/orderprocessor.jar

CMD ["java", "-jar", "/app/orderprocessor.jar"]