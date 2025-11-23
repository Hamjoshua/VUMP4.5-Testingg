FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY access-control.jar access-control.jar
ENTRYPOINT ["java", "-jar", "app.jar"]