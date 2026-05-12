FROM openjdk:17-jdk-slim
VOLUME /tmp
ARG JAR_FILE=build/libs/dinobite-backend-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /dinobite-backend.jar
ENTRYPOINT ["java", "-jar", "/dinobite-backend.jar"]
