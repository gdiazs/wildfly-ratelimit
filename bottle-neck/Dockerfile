FROM openjdk:21-slim-bullseye
COPY target/bottle-neck-0.0.1-SNAPSHOT.jar application.jar
EXPOSE 9080
ENTRYPOINT [ "java", "-jar", "application.jar" ]