FROM eclipse-temurin:17-jre-alpine as security_provider
RUN addgroup -S nonroot \
  && adduser -S nonroot -G nonroot
USER nonroot

WORKDIR /app

COPY access-control.jar access-control.jar
ENTRYPOINT ["java", "-jar", "app.jar"]