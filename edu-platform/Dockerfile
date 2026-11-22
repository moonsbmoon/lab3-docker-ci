FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Копируем библиотеку, так как она нужна edu-platform
COPY pricing-lib /app/pricing-lib
WORKDIR /app/pricing-lib
RUN mvn clean install -Dmaven.test.skip=true

WORKDIR /app
COPY edu-platform/pom.xml .
COPY edu-platform/src ./src
RUN mvn clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]