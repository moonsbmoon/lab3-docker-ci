FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY enrollment/pom.xml .
COPY enrollment/src ./src
RUN mvn clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]