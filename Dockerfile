FROM openjdk:11
LABEL maintainer="Fagner Lima <contato@fagnerlima.pro.br>"

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ADD target/sample-specification-api-*.jar /opt/sample-specification-api.jar
ENTRYPOINT ["java", "-Duser.timezone=GMT-03:00", "-jar", "/opt/sample-specification-api.jar"]

EXPOSE 8080