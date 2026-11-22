FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY payment/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]