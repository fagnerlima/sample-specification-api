# Build stage
FROM eclipse-temurin:21-jdk AS build

# The Maven Wrapper verifies the checksum of the .zip distribution, which needs unzip.
RUN apt-get update \
    && apt-get install -y --no-install-recommends unzip \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# The cache mount keeps Maven and the dependencies between builds, so a source change does not download them again.
RUN --mount=type=cache,target=/root/.m2 chmod +x mvnw && ./mvnw -B -ntp package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre AS production

LABEL author="Fagner Lima" \
      email="contato@fagnerlima.com" \
      description="Sample project with Spring Specification Tools" \
      license="GPLv3"

RUN groupadd --system app && useradd --system --gid app --no-create-home app

WORKDIR /app

COPY --from=build /app/target/sample-specification-api.jar app.jar

USER app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
