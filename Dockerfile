# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:23-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

# บังคับใช้ System Properties
ENTRYPOINT ["sh", "-c", "java \
    -Dspring.data.mongodb.uri=${SPRING_DATA_MONGODB_URI} \
    -Dspring.data.redis.host=${REDIS_HOST} \
    -Dspring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP_SERVERS} \
    -jar app.jar"]