#!/bin/bash
./mvnw clean install -DskipTests
docker build -t sample-specification-api .
