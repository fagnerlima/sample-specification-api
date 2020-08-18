# Build Stage
FROM openjdk:11.0.8-slim as build

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x mvnw & ./mvnw clean install -DskipTests

# Production Stage
FROM openjdk:11.0.8-slim as production

LABEL author="Fagner Lima" \
      email="contato@fagnerlima.pro.br" \
      description="Sample project with Spring Specification Tools" \
      license="GPLv3"

COPY --from=build /app/target/sample-specification-api-0.1.0.jar /app/

ENTRYPOINT ["java", "-jar", "/app/sample-specification-api-0.1.0.jar"]

EXPOSE 8080
