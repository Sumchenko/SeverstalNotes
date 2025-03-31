# Этап 1: Сборка приложения с Maven
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
RUN apt-get update && apt-get install -y maven
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Этап 2: Создание финального образа
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/SeverstalNotes-1.0-SNAPSHOT.jar app.jar
COPY src/main/resources/configDB.properties /app/configDB.properties
COPY src/main/resources/log4j.properties /app/log4j.properties
ENTRYPOINT ["java", "-jar", "app.jar"]