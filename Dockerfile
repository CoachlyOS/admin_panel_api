# syntax=docker/dockerfile:1

# ---- build stage ----
# JDK 25 + Maven. Builds the Spring Boot executable jar.
FROM maven:3.9.11-eclipse-temurin-25 AS build
WORKDIR /build

# Cache dependencies: only re-download when pom.xml changes.
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Build the app. Tests run in CI, not in the image.
COPY src ./src
RUN mvn -B package -DskipTests

# ---- runtime stage ----
# Slim JRE. Spring Boot ships a self-contained executable jar, so no app server needed.
FROM eclipse-temurin:25-jre-alpine AS runtime
WORKDIR /app

# Non-root process + a writable dir for the local storage provider (uploads).
RUN addgroup -S app && adduser -S -G app app \
 && mkdir -p /app/uploads \
 && chown -R app:app /app

# spring-boot:repackage replaces target/*.jar with the executable fat jar
# (the plain jar is left as *.jar.original, which the glob below does not match).
COPY --from=build /build/target/*.jar /app/app.jar

USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
